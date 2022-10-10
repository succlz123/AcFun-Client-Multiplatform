package org.succlz123.lib.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import org.succlz123.lib.screen.window.ScreenWindow
import org.succlz123.lib.screen.window.ScreenWindowSizeClass

@Composable
fun rememberIsWindowExpanded(): Boolean {
    val sizeClass = ScreenWindow.sizeClassFlow.collectAsState()
    return remember(sizeClass.value) { sizeClass.value == ScreenWindowSizeClass.Expanded }
}