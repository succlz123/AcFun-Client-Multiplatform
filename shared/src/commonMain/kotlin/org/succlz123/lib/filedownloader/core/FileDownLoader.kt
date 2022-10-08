package org.succlz123.lib.filedownloader.core

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.internal.toLongOrDefault
import org.succlz123.lib.filedownloader.core.http.HttpConnectionClient
import org.succlz123.lib.filedownloader.core.utils.FileDownloadLoaderLogger
import java.io.File
import kotlin.math.roundToInt

class FileDownLoader(rootDirectory: File) {

    companion object {
        val USER_DIR = File(System.getProperty("user.dir"))

        @Volatile
        var instance: FileDownLoader? = null

        fun configuration(rootDirectory: File = USER_DIR) {
            instance = FileDownLoader(rootDirectory)
        }

        fun instance(): FileDownLoader {
            val i = instance
            return if (i == null) {
                synchronized(FileDownLoader::class.java) {
                    instance ?: FileDownLoader(
                        USER_DIR
                    ).apply {
                        instance = this
                    }
                }
            } else {
                return i
            }
        }

        fun parserRecordFileOfContentLength(cacheDir: File, downloadState: DownloadState): Int {
            val downloadFile = downloadState.getDownloadFile(cacheDir) ?: return 0
            val map = parserRecordFile(downloadState.getRecordFile(cacheDir))
            if (!map["contentLength"].isNullOrEmpty()) {
                downloadState.contentLength = map["contentLength"]?.toIntOrNull() ?: 0
            } else if (!map["contentType"].isNullOrEmpty()) {
                downloadState.contentType = map["contentType"].orEmpty()
            }
            return if (downloadState.contentLength == 0) {
                0
            } else if (downloadFile.length() == downloadState.contentLength.toLong()) {
                100
            } else {
                (downloadFile.length().toFloat() * 100 / downloadState.contentLength).roundToInt()
            }
        }

        fun parserRecordFile(recordFile: File?): HashMap<String, String> {
            val map = hashMapOf<String, String>()
            if (recordFile == null || !recordFile.exists()) {
                return map
            }
            val downloadRecord = recordFile.readText()
            val parameters = downloadRecord.split("\n")
            for (parameter in parameters) {
                val kv = parameter.split("(=)")
                val k = kv.firstOrNull()
                val v = kv.lastOrNull()
                if (!k.isNullOrEmpty() && !v.isNullOrEmpty()) {
                    map[k] = v
                }
            }
            return map
        }
    }

    private val job = SupervisorJob()

    private val dispatcher: CoroutineDispatcher = newFixedThreadPoolContext(2, "file-downloader")

    private val scope = CoroutineScope(job)

    private var cacheDir: File

    private val client: HttpConnectionClient

    private val downloadingTask = HashMap<String, DownloadState>()

    init {
        cacheDir = File(rootDirectory, "file-download")
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                throw IllegalStateException("Could not create cache directory: ${cacheDir.absolutePath}")
            }
        }
        client = HttpConnectionClient()
    }

    private fun debugLog(msg: String) {
        FileDownloadLoaderLogger.debugLog("Thread: ${Thread.currentThread().name}, $msg")
    }

    fun get(request: DownloadRequest): DownloadState {
        val url = request.url
        if (url.isNullOrEmpty()) {
            debugLog("onError - Url is null or empty!")
            return DownloadState(request, MutableStateFlow(DownloadStateType.Error("Url is null or empty!")))
        }
        val requestKey = request.key()
        val hasWorkingTask = downloadingTask[requestKey]
        return if (hasWorkingTask == null) {
            val state = DownloadState(request)
            val curPos = state.getM3U8ListFile(cacheDir)
            val map = parserRecordFile(state.getRecordFile(cacheDir))
            if (curPos?.exists() == true) {
                state.progress.value = 100
                state.downloadStateType.value = DownloadStateType.Finish
            } else {
                val size = map["tsListSize"]?.toIntOrNull() ?: 0
                if (size == 0) {
                    state.downloadStateType.value = DownloadStateType.Error("")
                } else {
                    var count = 0
                    for (i in 0..size) {
                        val file = state.getTsFile(cacheDir, i)
                        if (file?.exists() == true) {
                            count++
                        } else {
                            break
                        }
                    }
                    state.progress.value = count * 100 / size
                    state.downloadStateType.value = DownloadStateType.Pause
                }
            }
            state
        } else {
            hasWorkingTask
        }
    }

    fun getAllDownloadFile(): List<DownloadRequest>? {
        return cacheDir.listFiles()?.filter { it.absolutePath.endsWith(".record") }?.mapNotNull {
            val map = parserRecordFile(it)
            if (map["title"].isNullOrEmpty() || map["url"].isNullOrEmpty()) {
                null
            } else {
                DownloadRequest(
                    map["id"].orEmpty(),
                    map["title"].orEmpty(),
                    map["image"].orEmpty(),
                    map["url"].orEmpty(),
                    map["tag"].orEmpty(),
                ).apply {
                    createTime = map["time"].orEmpty().toLongOrDefault(0L)
                    downloadFilePath = it.absolutePath.replace(".record", ".download")
                    m3u8FilePath = it.absolutePath.replace(".record", ".m3u8")
                }
            }
        }?.sortedBy {
            -it.createTime
        }
    }

    fun download(downloadState: DownloadState) {
        when (downloadState.downloadStateType.value) {
            is DownloadStateType.Starting, is DownloadStateType.Downloading, DownloadStateType.Finish -> {
            }

            is DownloadStateType.Uninitialized, is DownloadStateType.Error, is DownloadStateType.Pause -> {
                debugLog("pull (${downloadState.request.url})")
                downloadingTask[downloadState.request.key()] = downloadState
                downloadState.downloadStateType.value = DownloadStateType.Starting
                downloadState.downloadJob = scope.launch(client.dispatcher()) {
                    client.tsFileDownload(this, cacheDir, downloadState) { progress, error ->
                        debugLog("progress: $progress")
                        if (this.isActive) {
                            scope.launch {
                                if (error) {
                                    downloadState.downloadStateType.value = DownloadStateType.Error("")
                                    downloadingTask.remove(downloadState.request.key())
                                } else {
                                    downloadState.progress.value = progress
                                    if (progress >= 100) {
                                        downloadState.downloadStateType.value = DownloadStateType.Finish
                                        downloadingTask.remove(downloadState.request.key())
                                    } else {
                                        downloadState.downloadStateType.value = DownloadStateType.Downloading
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun pause(downloadState: DownloadState) {
        downloadState.downloadJob?.cancel()
        downloadState.downloadJob = null
        downloadingTask.remove(downloadState.request.key())
        downloadState.downloadStateType.value = DownloadStateType.Pause
    }

    fun stop(downloadState: DownloadState) {
        downloadState.downloadJob?.cancel()
        downloadState.downloadJob = null
        cacheDir.listFiles()?.filter {
            it.name.contains(downloadState.request.key())
        }?.forEach {
            it.delete()
        }
        downloadState.contentLength = 0
        downloadState.contentType = ""
        downloadingTask.remove(downloadState.request.key())
        downloadState.progress.value = 0
        downloadState.downloadStateType.value = DownloadStateType.Uninitialized
    }

    fun shutdown() {
        job.cancel()
    }

    fun shutdownAndClearEverything() {
        shutdown()
        clearCache()
    }

    fun clearCache() {
        scope.launch {
            cacheDir.deleteOnExit()
        }
    }
}