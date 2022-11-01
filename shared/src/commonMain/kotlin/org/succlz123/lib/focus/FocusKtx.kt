package org.succlz123.lib.focus

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.*
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.debugInspectorInfo
import org.succlz123.app.acfun.ui.main.GlobalFocusViewModel
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.lifecycle.ScreenLifecycle
import org.succlz123.lib.screen.viewmodel.viewModel

val allowMove = { true }

val notAllowMove = { false }

var lastNotify = 0L

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Modifier.onFocusKeyEventMove(
    leftCanMove: () -> Boolean = allowMove,
    upCanMove: () -> Boolean = allowMove,
    rightCanMove: () -> Boolean = allowMove,
    downCanMove: () -> Boolean = allowMove,
    backMove: () -> Boolean = { false },
): Modifier = composed(inspectorInfo = debugInspectorInfo {
    name = "onFocusKeyEventMove"
    properties["leftCanMove"] = leftCanMove
    properties["UpCanMove"] = upCanMove
    properties["rightCanMove"] = rightCanMove
    properties["downCanMove"] = downCanMove
}) {
    val focusManager = LocalFocusManager.current
    val screen = LocalScreenRecord.current
    val screenNavigator = LocalScreenNavigator.current
    Modifier.onKeyEvent { keyEvent ->
        if (keyEvent.type == KeyEventType.KeyDown) {
            if (System.currentTimeMillis() - lastNotify < 160L) {
                return@onKeyEvent true
            }
            lastNotify = System.currentTimeMillis()
            when (keyEvent.key) {
                Key.A, Key.DirectionLeft -> {
                    if (leftCanMove.invoke()) {
                        val result = focusManager.moveFocus(FocusDirection.Left)
                        println("onFocusKeyEventMove Left - $result")
                    }
                    true
                }

                Key.W, Key.DirectionUp -> {
                    if (upCanMove.invoke()) {
                        val result = focusManager.moveFocus(FocusDirection.Up)
                        println("onFocusKeyEventMove Up - $result")
                    }
                    true
                }

                Key.D, Key.DirectionRight -> {
                    if (rightCanMove.invoke()) {
                        val result = focusManager.moveFocus(FocusDirection.Right)
                        println("onFocusKeyEventMove Right - $result")
                    }
                    true
                }

                Key.S, Key.DirectionDown -> {
                    if (downCanMove.invoke()) {
                        val result = focusManager.moveFocus(FocusDirection.Down)
                        println("onFocusKeyEventMove Down - $result")
                    }
                    true
                }

                Key.Back, Key.Escape -> {
                    if (screen.hostLifecycle.getCurrentState() == ScreenLifecycle.State.RESUMED) {
                        val result = backMove.invoke()
                        if (!result) {
                            screenNavigator.pop()
                        }
                        result
                    } else {
                        false
                    }
                }

                else -> {
                    false
                }
            }
        } else {
            true
        }
    }
}

@Composable
fun Modifier.onFocusParent(
    thisRequester: FocusRequester, tag: String = "", focusCb: ((FocusState) -> Unit)? = null
): Modifier = composed(inspectorInfo = debugInspectorInfo {
    name = "onFocusParent"
    properties["thisRequester"] = thisRequester
}) {
    val focusVm = viewModel(GlobalFocusViewModel::class) {
        GlobalFocusViewModel()
    }
    Modifier.focusRequester(thisRequester).onFocusChanged {
        if (it.hasFocus) {
            focusVm.curFocusRequesterParent.value = thisRequester
            println("onFocusParent: $thisRequester has $it $tag")
        }
        focusCb?.invoke(it)
    }.focusTarget()
}

