package org.succlz123.lib.filedownloader.core;

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class DownloadRequest(
    val id: String? = null,
    val title: String? = null,
    val image: String? = null,
    val url: String? = null,
    val tag: String? = null
) {

    var createTime: Long = 0

    var downloadFilePath: String? = null

    var m3u8FilePath: String? = null

    fun key(): String {
        return hashKey(id.orEmpty())
    }
}

internal fun hashKey(key: String): String {
    val cacheKey: String = try {
        val mDigest = MessageDigest.getInstance("MD5")
        mDigest.update(key.toByteArray())
        bytesToHexString(mDigest.digest())
    } catch (e: NoSuchAlgorithmException) {
        key.hashCode().toString()
    }
    return cacheKey
}

internal fun bytesToHexString(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (i in bytes.indices) {
        val hex = Integer.toHexString(0xFF and bytes[i].toInt())
        if (hex.length == 1) {
            sb.append('0')
        }
        sb.append(hex)
    }
    return sb.toString()
}