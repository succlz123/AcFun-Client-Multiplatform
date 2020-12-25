package org.succlz123.lib.image

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
actual fun AsyncImageUrlMultiPlatform(modifier: Modifier, url: String, contentScale: ContentScale) {
    if (url.startsWith("http") || url.startsWith("/")) {
        AsyncImage(
            modifier = modifier, model = url, contentDescription = null, contentScale = contentScale
        )
    } else {
        val context = LocalContext.current
        AsyncImage(
            modifier = modifier,
            model = ImageRequest.Builder(LocalContext.current)
//                .data(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/assets/" + url))
                .data(Uri.parse("file:///android_asset/$url"))
                .build(),
            contentDescription = null,
            contentScale = contentScale
        )
    }
}