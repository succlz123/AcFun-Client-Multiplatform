package org.succlz123.lib.network

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Throws(IOException::class)
fun URL.openConnection(): HttpURLConnection {
    val connection = this.openConnection() as HttpURLConnection
    connection.connectTimeout = 10000
    connection.readTimeout = 10000
    return connection
}

@Throws(IOException::class)
@JvmOverloads
fun String.openConnection(
    ua: String?,
    connectTimeout: Int = 6000,
    readTimeout: Int = 6000
): HttpURLConnection {
    val connection = URL(this).openConnection() as HttpURLConnection
    if (ua != null) {
        connection.setRequestProperty("User-Agent", ua)
    }
    connection.connectTimeout = connectTimeout
    connection.readTimeout = readTimeout
    return connection
}

object HttpX {

    fun doGet(urlStr: String?): String? {
        var url: URL?
        var conn: HttpURLConnection? = null
        var inputStream: InputStream? = null
        var baos: ByteArrayOutputStream? = null
        try {
            url = URL(urlStr)
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("accept", "*/*")
            conn.setRequestProperty("connection", "Keep-Alive")
            conn.setRequestProperty(
                "user-agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36"
            )
            return if (conn.responseCode == 200) {
                inputStream = conn.inputStream
                baos = ByteArrayOutputStream()
                var len = -1
                val buf = ByteArray(1024 * 8)
                while (inputStream.read(buf).also { len = it } != -1) {
                    baos.write(buf, 0, len)
                }
                baos.flush()
                baos.toString("UTF-8")
            } else {
                throw RuntimeException(" responseCode is not 200 ... ")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
            } catch (ignored: IOException) {
            }
            try {
                baos?.close()
            } catch (ignored: IOException) {
            }
            conn?.disconnect()
        }
        return null
    }
}