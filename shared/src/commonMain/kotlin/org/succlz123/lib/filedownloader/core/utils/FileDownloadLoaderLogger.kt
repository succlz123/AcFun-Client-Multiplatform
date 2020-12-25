package org.succlz123.lib.filedownloader.core.utils

object FileDownloadLoaderLogger {
    private const val TAG = "FileDownloadLoaderLogger"

    var isDebug: Boolean = true

    fun debugLog(msg: String) {
        if (isDebug) {
            println("$TAG: $msg")
        }
    }
}