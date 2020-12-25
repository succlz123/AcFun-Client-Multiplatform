package org.succlz123.lib.image

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import org.succlz123.lib.imageloader.ImageAsyncImageFile
import org.succlz123.lib.imageloader.ImageAsyncImageUrl
import org.succlz123.lib.imageloader.ImageRes
import org.succlz123.lib.imageloader.core.ImageCallback

@Composable
actual fun AsyncImageUrlMultiPlatform(modifier: Modifier, url: String, contentScale: ContentScale) {
    if (url.startsWith("http")) {
        ImageAsyncImageUrl(url = url, imageCallback = ImageCallback {
            Image(
                painter = it, contentDescription = url, modifier = modifier, contentScale = contentScale
            )
        })
    } else if (url.startsWith("/")) {
        ImageAsyncImageFile(filePath = url, imageCallback = ImageCallback {
            Image(
                painter = it, contentDescription = url, modifier = modifier, contentScale = contentScale
            )
        })
    } else {
        ImageRes(resName = url, imageCallback = ImageCallback {
            Image(
                painter = it, contentDescription = url, modifier = modifier, contentScale = contentScale
            )
        })
    }
}