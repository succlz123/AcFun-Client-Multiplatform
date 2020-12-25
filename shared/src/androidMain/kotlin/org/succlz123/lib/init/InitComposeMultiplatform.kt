package org.succlz123.lib.init

import android.content.Context
import android.os.Environment
import android.os.StatFs
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.succlz123.lib.filedownloader.core.FileDownLoader
import java.io.File

@Composable
actual fun initComposeMultiplatform() {
    val context = LocalContext.current
    FileDownLoader.configuration(rootDirectory = context.cacheDir!!)
}

fun getCachePath(context: Context): File {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
        || !Environment.isExternalStorageRemovable()
    ) {
        context.cacheDir ?: context.getDir(
            "Download",
            Context.MODE_PRIVATE
        )
    } else {
        context.getDir(
            "Download",
            Context.MODE_PRIVATE
        )
    }
}

fun getAvailableInternalMemorySize(): Long {
    val path = Environment.getDataDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSize.toLong()
    val availableBlocks = stat.availableBlocks.toLong()
    return availableBlocks * blockSize
}

fun getTotalInternalMemorySize(): Long {
    val path = Environment.getDataDirectory()
    val stat = StatFs(path.path)
    val blockSize = stat.blockSize.toLong()
    val totalBlocks = stat.blockCount.toLong()
    return totalBlocks * blockSize
}

fun hasSDCard(): Boolean {
    return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
}

fun getAvailableExternalMemorySize(): Long {
    return if (hasSDCard()) {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val availableBlocks = stat.availableBlocks.toLong()
        availableBlocks * blockSize
    } else {
        -1
    }
}

fun getTotalExternalMemorySize(): Long {
    return if (hasSDCard()) {
        val path = Environment.getExternalStorageDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalBlocks = stat.blockCount.toLong()
        totalBlocks * blockSize
    } else {
        -1
    }
}