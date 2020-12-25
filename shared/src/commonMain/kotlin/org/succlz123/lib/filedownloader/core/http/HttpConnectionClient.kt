package org.succlz123.lib.filedownloader.core.http

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.newFixedThreadPoolContext
import org.succlz123.lib.filedownloader.core.DownloadState
import org.succlz123.lib.filedownloader.core.utils.FileDownloadLoaderLogger
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL

class HttpConnectionClient(
    private val threadPool: CoroutineDispatcher = newFixedThreadPoolContext(2, "Downloader"),
) {

    fun dispatcher(): CoroutineDispatcher {
        return threadPool
    }

    suspend fun tsFileDownload(
        scope: CoroutineScope,
        cacheDir: File,
        downloadState: DownloadState,
        cb: (Int, Boolean) -> Unit
    ) {
        val url = downloadState.request.url.orEmpty()

        val recordFile = downloadState.getRecordFile(cacheDir) ?: return
        val m3u8File = downloadState.getM3U8ListFile(cacheDir) ?: return
        if (!recordFile.exists()) {
            recordFile.createFileKtx()
        }
        downloadState.contentLength = 0
        cb.invoke(0, false)

        try {
            val m3U8Data = getM3U8Data(url)
            if (m3U8Data == null) {
                return
            }
            if (!scope.isActive) {
                return
            }
            recordFile.outputStream().write(
                ("id(=)${downloadState.request.id}\n" + "title(=)${downloadState.request.title}\n" + "image(=)${downloadState.request.image}\n" + "url(=)${downloadState.request.url}\n" + "tag(=)${downloadState.request.tag}\n" + "time(=)${System.currentTimeMillis()}\n" + "tsListSize(=)${m3U8Data.info.size}\n").toByteArray(
                    Charsets.UTF_8
                )
            )
            m3U8Data.info.forEachIndexed { index, s ->
                debugLog("ts file url: " + s)
                if (!scope.isActive) {
                    return
                }
                val tsFile = downloadState.getTsFile(cacheDir, index)
                if (tsFile == null) {
                    debugLog("tsFile <---")
                    cb.invoke(0, true)
                    return
                }
                val isSuccess = downloadTsFile(s.text, tsFile, downloadState)
                if (!isSuccess) {
                    cb.invoke(((index) / m3U8Data.info.size.toFloat() * 100).toInt(), true)
                    return
                }
                s.localFilePath = tsFile.name
                cb.invoke(((index + 1) / m3U8Data.info.size.toFloat() * 100).toInt(), false)
                if (index == m3U8Data.info.size - 1) {
                    if (!m3u8File.exists()) {
                        m3u8File.createFileKtx()
                    }
                    m3u8File.outputStream().write((m3U8Data.toString()).toByteArray(Charsets.UTF_8))
                }
            }
        } catch (error: Throwable) {
            error.printStackTrace()
        } finally {
        }
    }

    private fun debugLog(msg: String) {
        FileDownloadLoaderLogger.debugLog("Thread: ${Thread.currentThread().name}, $msg")
    }

    @Throws(IOException::class)
    private fun String.openConnection(
        ua: String?, connectTimeout: Int = 6000, readTimeout: Int = 6000
    ): HttpURLConnection {
        val connection = URL(this).openConnection() as HttpURLConnection
        if (ua != null) {
            connection.setRequestProperty("User-Agent", ua)
        }
        connection.connectTimeout = connectTimeout
        connection.readTimeout = readTimeout
        return connection
    }

    private fun getM3U8Data(url: String): M3U8Data? {
        val conn = url.openConnection(null)
        conn.requestMethod = "GET"
        val tsType = conn.contentType
        var m3U8Data: M3U8Data? = null
        if (tsType == "application/vnd.apple.mpegurl") {
            m3U8Data = processM3U8urlFile(conn.inputStream.bufferedReader().readText())
            val tsVideoUrl: String = m3U8Data.info.firstOrNull()?.text.orEmpty()
            if (!tsVideoUrl.startsWith("http:") && !tsVideoUrl.startsWith("https:")) {
                for (relativeTSurls in m3U8Data.info) {
                    val absoluteTS = getAbsolutePath(url, relativeTSurls.text)
                    relativeTSurls.text = absoluteTS
                }
            }
            return m3U8Data
        } else {
            debugLog("Content-type is not ts!")
            return null
        }
    }

    fun processM3U8urlFile(m3u8Text: String): M3U8Data {
        val m3u8Data = M3U8Data()
        var nextIsTsUrl = false
        var tsDuration = ""
        val list: MutableList<M3U8Data.TSFile> = ArrayList()
        for (s in m3u8Text.split("\n")) {
            if (s.startsWith("#EXT-X-VERSION")) {
                m3u8Data.version = s.split(":").lastOrNull().orEmpty()
            } else if (s.startsWith("#EXT-X-TARGETDURATION")) {
                m3u8Data.duration = s.split(":").lastOrNull().orEmpty()
            } else if (s.startsWith("#EXT-X-MEDIA-SEQUENCE")) {
                m3u8Data.sequence = s.split(":").lastOrNull().orEmpty()
            } else if (s.startsWith("#EXTINF:")) {
                nextIsTsUrl = true
                tsDuration = s.split(":").lastOrNull().orEmpty()
            } else if (nextIsTsUrl) {
                nextIsTsUrl = false
                list.add(M3U8Data.TSFile(tsDuration, s))
                tsDuration = ""
            }
        }
//        val m = Pattern.compile(".*\\.ts.*", Pattern.MULTILINE).matcher(m3u8Text)
//        val isRelativePath = false
//        while (m.find()) {
//            val tsVideoUrl: String = m.group()
//            list.add(tsVideoUrl)
//        }
        m3u8Data.info = list
        return m3u8Data
    }

    fun getAbsolutePath(baseAbsolutePath: String, relativePath: String): String {
        val idx = baseAbsolutePath.lastIndexOf("/")
        val base = baseAbsolutePath.substring(0, idx + 1)
        return base + relativePath
    }

    fun downloadTsFile(url: String, tsFile: File, downloadState: DownloadState): Boolean {
        var conn: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var out: RandomAccessFile? = null
        var isOk: Boolean = false
        try {
            debugLog("ts file url: " + url)
            val range = tsFile.length()
            conn = url.openConnection(null)
            conn.requestMethod = "GET"
//            debugLog("Range is ($range)!")
//            if (range > 0) {
//                conn.setRequestProperty("Range", "bytes=$range-")
//            }
            if (conn.responseCode != 200) {
                debugLog("Response status code is (${conn.responseCode})!")
                return false
            }
            val contentTypeString = conn.contentType
            if (downloadState.contentType.isEmpty()) {
                downloadState.contentType = contentTypeString.orEmpty()
            }
            if (contentTypeString == null) {
                debugLog("Content-type is null!")
                return false
            }
            val contentLength = conn.contentLength
            downloadState.contentLength += contentLength
            if (contentLength <= 0) {
                debugLog("Content length is null!")
                return false
            }
            out = RandomAccessFile(tsFile.absolutePath, "rwd")
            if (contentLength != out.length().toInt()) {
                inputStream = conn.inputStream
                val input = inputStream ?: return false
                out.seek(range)
                val buf = ByteArray(8 * 1024)
                var len = 0
                while (input.read(buf).also { len = it } != -1) {
                    out.write(buf, 0, len)
                }
            }
            isOk = true
        } catch (error: Throwable) {
            error.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: IOException) {
            }
            conn?.disconnect()
            try {
                out?.close()
            } catch (ignored: IOException) {
            }
            if (!isOk) {
                tsFile.delete()
            }
        }
        return isOk
    }
}

data class M3U8Data(
    var version: String = "", var duration: String = "", var sequence: String = "", var info: List<TSFile> = emptyList()
) {

    var isRelativePath: Boolean = false

    data class TSFile(val info: String, var text: String, var localFilePath: String = "")

    override fun toString(): String {
        return """#EXTM3U
#EXT-X-VERSION:$version
#EXT-X-TARGETDURATION:$duration
#EXT-X-MEDIA-SEQUENCE:$sequence
${info.joinToString(separator = "") { "#EXTINF:" + it.info + "\n" + it.localFilePath + "\n" }}
#EXT-X-ENDLIST
        """
    }
}

fun File.createFileKtx() {
    if (!this.parentFile.exists()) {
        this.parentFile.mkdirs()
    }
    if (!this.exists()) {
        try {
            this.createNewFile()
        } catch (e: Exception) {
        }
    }
}
