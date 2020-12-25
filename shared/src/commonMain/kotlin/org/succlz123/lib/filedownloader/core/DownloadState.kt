package org.succlz123.lib.filedownloader.core

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class DownloadState(
    var request: DownloadRequest,
    var downloadStateType: MutableStateFlow<DownloadStateType> = MutableStateFlow(DownloadStateType.Uninitialized)
) {

    var progress: MutableStateFlow<Int> = MutableStateFlow(0)

    var downloadJob: Job? = null

    var contentLength: Int = 0

    var contentType: String = ""

    fun getRecordFile(cacheDir: File): File? {
        if (request.key().isEmpty()) {
            return null
        }
        return File(cacheDir, "${request.key()}.record")
    }

    fun getTsFile(cacheDir: File, index: Int): File? {
        if (request.key().isEmpty()) {
            return null
        }
        return File(cacheDir, "${request.key()}_${index}.ts")
    }

    fun getM3U8ListFile(cacheDir: File): File? {
        if (request.key().isEmpty()) {
            return null
        }
        return File(cacheDir, "${request.key()}.m3u8")
    }

    fun getDownloadFile(cacheDir: File): File? {
        if (request.key().isEmpty()) {
            return null
        }
        return File(cacheDir, "${request.key()}.download")
    }
}

sealed class DownloadStateType {

    object Uninitialized : DownloadStateType()

    object Starting : DownloadStateType()

    object Downloading : DownloadStateType()

    object Finish : DownloadStateType()

    object Pause : DownloadStateType()

    data class Error(val errorMsg: String) : DownloadStateType()
}
