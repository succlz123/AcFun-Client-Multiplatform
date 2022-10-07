import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import org.succlz123.app.acfun.SharedApp
import org.succlz123.lib.init.initComposeMultiplatform
import org.succlz123.lib.screen.ScreenContainer
import java.awt.Dimension

fun main() = application {
    val size = remember { DpSize(1080.dp, 720.dp) }
    val windowState = rememberWindowState(size = size)
    initComposeMultiplatform()
    ScreenContainer(
        enableEscBack = true,
        resizable = true,
        state = windowState,
        minimumSize = Dimension(480.dp.value.toInt(), 720.dp.value.toInt()),
        visible = true,
        undecorated = false,
        transparent = false,
        icon = painterResource("ic_acfun.png"),
        onCloseRequest = {
            exitApplication()
        },
    ) {
        window.rootPane.apply {
            rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
        }
        SharedApp()
        AppWindowTitleBar(windowState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WindowScope.AppWindowTitleBar(
    windowState: WindowState
) = WindowDraggableArea(
    modifier = Modifier.combinedClickable(interactionSource = remember { MutableInteractionSource() },
        indication = null,
        onDoubleClick = {
            windowState.placement = when (windowState.placement) {
                WindowPlacement.Floating -> WindowPlacement.Maximized
                WindowPlacement.Maximized -> WindowPlacement.Floating
                WindowPlacement.Fullscreen -> WindowPlacement.Floating
            }
        },
        onClick = {})
) {
    Box(
        Modifier.fillMaxWidth().padding(0.dp, ((64 - 25) / 2).dp), contentAlignment = Alignment.Center
    ) {
    }
}

