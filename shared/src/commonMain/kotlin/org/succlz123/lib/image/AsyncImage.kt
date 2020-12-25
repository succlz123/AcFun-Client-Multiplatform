package org.succlz123.lib.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale


@Composable
expect fun AsyncImageUrlMultiPlatform(modifier: Modifier, url: String, contentScale: ContentScale = ContentScale.Crop)