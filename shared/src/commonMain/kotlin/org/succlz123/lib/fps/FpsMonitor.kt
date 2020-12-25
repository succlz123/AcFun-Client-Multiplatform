package org.succlz123.lib.fps

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import org.succlz123.lib.common.getAppPlatformName

@Composable
fun FpsMonitor(modifier: Modifier) {
    var fpsCount by remember { mutableStateOf(0) }
    var fps by remember { mutableStateOf(0) }
    var lastUpdate by remember { mutableStateOf(0L) }
    val platformName = remember {
        getAppPlatformName() + " " + System.getProperty("os.name", "generic")
    }
    Text(
        text = "$platformName\nFPS: $fps",
        modifier = modifier,
        color = Color.Green,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.body1
    )
    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { ms ->
                fpsCount++
                if (fpsCount == 5) {
                    fps = (5000 / (ms - lastUpdate)).toInt()
                    lastUpdate = ms
                    fpsCount = 0
                }
            }
        }
    }
}