package org.succlz123.app.acfun.ui.video

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.ParagraphIntrinsics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import org.succlz123.app.acfun.api.bean.VideoContent
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.danmaku.DanmakuBean
import org.succlz123.app.acfun.danmaku.DanmkuAnimationState
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.operation.ScreenStackState
import org.succlz123.lib.screen.value
import org.succlz123.lib.screen.viewmodel.sharedViewModel
import org.succlz123.lib.screen.window.ScreenWindow
import org.succlz123.lib.video.*
import kotlin.math.roundToInt


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VideoPlayerScreen() {
    val screenNavigator = LocalScreenNavigator.current
    val screenRecord = LocalScreenRecord.current
    val videoContent = screenRecord.arguments.value<VideoContent?>("KEY_VIDEO_CONTENT")
    val localVideoTitle = screenRecord.arguments.value<String?>("KEY_VIDEO_LOCAL_TITLE")
    val localVideoFilePath = screenRecord.arguments.value<String?>("KEY_VIDEO_LOCAL_FILE")
    if (videoContent == null && localVideoFilePath == null) {
        screenNavigator.pop()
        return
    }
    if (screenRecord.stackStateFlow.value != ScreenStackState.IN_STACK) {
        return
    }
    val playerViewModel = sharedViewModel {
        VideoPlayerViewModel()
    }
    LaunchedEffect(Unit) {
        videoContent?.danmakuList?.danmakus?.forEach {
            it.isShow.value = false
        }
    }
    val focusRequester = remember { FocusRequester() }
    SideEffect {
        focusRequester.requestFocus()
    }

    val representations = videoContent?.playerList?.adaptationSet?.firstOrNull()?.representation
    val title = videoContent?.title ?: localVideoTitle.orEmpty()
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black).onKeyEvent { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when (keyEvent.key) {
                    Key.Spacebar, Key.Enter, Key.DirectionCenter -> {
                        if (playerViewModel.videoPlayerState.value is VideoPlayerState.Playing) {
                            playerViewModel.playerAction.value = VideoPlayerAction.Pause
                        } else {
                            playerViewModel.playerAction.value = VideoPlayerAction.Play
                        }
                        playerViewModel.showControllerCover.value = true
                        true
                    }

                    Key.DirectionLeft, Key.ButtonThumbLeft -> {
                        if (playerViewModel.playerAction.value !is VideoPlayerAction.Seek) {
                            playerViewModel.playerAction.value = VideoPlayerAction.Seek(
                                Math.max(
                                    playerViewModel.time.value - 5000L, 0
                                )
                            )
                        } else {
                            playerViewModel.playerAction.value = VideoPlayerAction.Seek(
                                Math.max(
                                    playerViewModel.playerAction.value.seekTime - 5000L, 0
                                )
                            )
                        }
                        playerViewModel.showControllerCover.value = true
                        true
                    }

                    Key.DirectionRight, Key.ButtonThumbRight -> {

                        if (playerViewModel.playerAction.value !is VideoPlayerAction.Seek) {
                            playerViewModel.playerAction.value = VideoPlayerAction.Seek(
                                Math.min(
                                    playerViewModel.time.value + 5000L, playerViewModel.duration.value
                                )
                            )
                        } else {
                            playerViewModel.playerAction.value = VideoPlayerAction.Seek(
                                Math.min(
                                    playerViewModel.playerAction.value.seekTime + 5000L, playerViewModel.duration.value
                                )
                            )
                        }
                        playerViewModel.showControllerCover.value = true
                        true
                    }

                    Key.Back, Key.Escape -> {
                        screenNavigator.pop()
                        true
                    }

                    else -> {
                        false
                    }
                }
            } else {
                true
            }
        }.focusRequester(focusRequester).focusTarget()
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black))
        if (representations.isNullOrEmpty() && localVideoFilePath.isNullOrEmpty()) {
            LoadingView()
        } else {
            playerViewModel.playList = representations
            playerViewModel.curPlayerLocalSource.value = localVideoFilePath.orEmpty()
            playerViewModel.curPlayerSource.value = representations?.firstOrNull()

            val result = VideoPlayer(Modifier, playerViewModel)
            if (result.isNotEmpty()) {
                screenNavigator.toast(result, time = 5000L)
                screenNavigator.pop()
            }
            VideoPlayerCover(title, false, playerViewModel)
            if (playerViewModel.playerSetting.danmaku.collectAsState().value) {
                SimpleDanmaku(playerViewModel, videoContent?.danmakuList?.danmakus)
            }
        }
    }
}

const val DANMAKU_SPEED = 12000

@Composable
fun SimpleDanmaku(
    playerViewModel: VideoPlayerViewModel,
    danmaku: List<DanmakuBean>?,
) {
    if (danmaku.isNullOrEmpty()) {
        return
    }
    val showTime = DANMAKU_SPEED
    val offset = remember { Offset(2.0f, 5.0f) }
    val screenSize = ScreenWindow.sizeFlow.collectAsState().value

    var recordTime = remember { 0L }
    val curTime = playerViewModel.time.collectAsState().value

    LaunchedEffect(curTime) {
        if (curTime < recordTime) {
            playerViewModel.currentWorkDanmaku.value.clear()
        }
        val out = danmaku.filter { it.position < curTime && curTime < (it.position + showTime) }
        var worker = playerViewModel.currentWorkDanmaku.value
        for (danmakuBean in out) {
            if (!worker.contains(danmakuBean)) {
                worker.add(danmakuBean)
                danmakuBean.showIndex = worker.size - 1
            }
            danmakuBean.workState.value = DanmkuAnimationState.IN_WORK
        }
//        worker.forEachIndexed { index, danmaku ->
//            if (danmaku.showIndex == -1) {
//                danmaku.showIndex = index
//            }
//        }
//        worker = worker.filter { it.position < curTime && curTime < (it.position + showTime * 2) }
//            .toMutableList()
//        println("out - " + out.size)
//        println("worker - " + worker.size)
        playerViewModel.currentWorkDanmaku.value = worker
        recordTime = curTime
    }

//    val transitionState = remember {
//        MutableTransitionState(screenSize.width.toInt()).apply {
//            targetState = -screenSize.width.toInt() / 2
//        }
//    }
//    val transition = updateTransition(transitionState, label = "height transition")
//    val width by transition.animateInt(
//        transitionSpec = {
////            repeatable(
////                iterations = 5111, animation = tween(
////                    durationMillis = 8000, easing = LinearEasing
////                ), repeatMode = RepeatMode.Restart
////            )
//            tween(
//                durationMillis = 8000, easing = LinearEasing
//            )
////            infiniteRepeatable(
////                animation = tween(
////                    durationMillis = 8000, easing = LinearEasing
////                ), repeatMode = RepeatMode.Restart, initialStartOffset = StartOffset(-1)
////            )
//        }, label = "width animation of danmaku"
//    ) { it }
//
//    val currentWorkDanmaku = playerViewModel.currentWorkDanmaku.collectAsState().value
//    Layout(modifier = Modifier.fillMaxSize().padding(48.dp), content = {
//        currentWorkDanmaku.forEach {
//            DanmakuText(modifier = Modifier, it, offset, screenSize)
//        }
//    }) { measurables, constraints ->
//        val placeables = measurables.map { it.measure(constraints) }
//        layout(constraints.maxWidth, constraints.maxHeight) {
//            placeables.forEachIndexed { index, placeable ->
//                val currentDanmaku = currentWorkDanmaku[index]
//                val now = curTime - currentDanmaku.position
//                val locationPer = screenSize.width / 5000
//                placeable.placeRelative(
////                    x = (constraints.maxWidth - now * locationPer).toInt(),
//                    (width + (placeables.getOrNull(index - 1)?.width ?: 0) + 32.dp.value.toInt()).toInt(),
//                    y = (((48 * density.density) + (currentDanmaku.showIndex * 82.dp).value) % (screenSize.height - (48 + 82 * 2 + 82 * 2).dp.value)).toInt()
//                )
//            }
//        }
//    }
//


    Box(modifier = Modifier.fillMaxSize().padding(48.dp)) {
        val currentWorkDanmaku = playerViewModel.currentWorkDanmaku.collectAsState().value
//        println("real worker - " + currentWorkDanmaku.size)
        currentWorkDanmaku.forEachIndexed { index, danmakuBean ->
            DanmakuText(modifier = Modifier, danmakuBean, index, currentWorkDanmaku, offset, screenSize)
        }
    }

//    val currentWorkDanmaku = playerViewModel.currentWorkDanmaku.collectAsState().value
//    Layout(modifier = Modifier.fillMaxSize().padding(48.dp), content = {
////        println("real worker - " + currentWorkDanmaku.size)
//        for (danmakuBean in currentWorkDanmaku) {
//            DanmakuText(modifier = Modifier, danmakuBean, offset, screenSize)
//        }
//    }) { measurables, constraints ->
//        val placeables = measurables.map { it.measure(constraints) }
//
//        layout(constraints.maxWidth, constraints.maxHeight) {
//            placeables.forEachIndexed { index, placeable ->
//                val currentDanmaku = currentWorkDanmaku[index]
//                currentDanmaku.width = placeable.width
////                placeable.placeRelative(
//////                    x = (constraints.maxWidth - now * locationPer).toInt(),
////                    (width + (placeables.getOrNull(index - 1)?.width ?: 0) + 32.dp.value.toInt()).toInt(),
////                    y = (((48 * density.density) + (currentDanmaku.showIndex * 82.dp).value) % (screenSize.height - (48 + 82 * 2 + 82 * 2).dp.value)).toInt()
////                )
//            }
//        }
//    }

//    Box(modifier = Modifier.fillMaxSize().padding(48.dp)) {
//        val currentWorkDanmaku = playerViewModel.currentWorkDanmaku.collectAsState().value
//        println("currentWorkDanmaku - " + currentWorkDanmaku.size)
//        currentWorkDanmaku.forEachIndexed { index, danmaku ->
////            println("danmaku - " + danmaku)
//            val type = getDanmakuType(danmaku.danmakuType)
//            when (type) {
//                BaseDanmaku.R2LDanmaku, BaseDanmaku.SpecialDanmaku -> {
//                    val xState = AnimationDanmakuXState(danmaku.isShow, showTime, screenSize.width.toInt(), true)
//                    DanmakuText(modifier = Modifier.absoluteOffset {
//                        if (xState.value <= (-(screenSize.width.toInt()) / 2)) {
////                            danmaku.isShow.value = false
//                        }
//                        IntOffset(
//                            x = xState.value,
//                            y = (((48 * density.density) + (danmaku.showIndex * 82.dp).value) % (screenSize.height - (48 + 82 * 2 + 82 * 2).dp.value)).toInt()
//                        )
//                    }, danmaku, offset)
//                }
//
//                BaseDanmaku.L2RDanmaku -> {
//                    val xState = AnimationDanmakuXState(danmaku.isShow, showTime, screenSize.width.toInt(), false)
//                    DanmakuText(modifier = Modifier.absoluteOffset {
//                        IntOffset(
//                            x = xState.value,
//                            y = (((48 * density.density) + (danmaku.showIndex * 82.dp).value) % (screenSize.height - (48 + 82 * 2 + 82 * 2).dp.value)).toInt()
//                        )
//                    }, danmaku, offset)
//                }
//
//                BaseDanmaku.FBDanmaku -> {
//                    DanmakuText(modifier = Modifier.align(Alignment.BottomCenter), danmaku, offset)
//                }
//
//                BaseDanmaku.FTDanmaku -> {
//                    DanmakuText(modifier = Modifier.align(Alignment.TopCenter), danmaku, offset)
//                }
//            }
//            SideEffect {
//                danmaku.isShow.value = true
//            }
//        }
//    }
}

@Composable
fun AnimationDanmakuXState(isShow: State<Boolean>, showTime: Int, screenSize: Int, isRight2Left: Boolean): State<Int> {
//                    MeasureUnconstrainedViewWidth(viewToMeasure = {
//                        Text(danmaku.body.orEmpty())
//                    }) {
//
//                    }
//                    val now = curTime - danmaku.position
//                    var currentState = remember { mutableStateOf(now) }.value
//                    val locationPer = screenSize.width / 5000


    val returnResult = animateIntAsState(
        targetValue = if (isShow.value) {
            -(screenSize) / 2
        } else {
            screenSize
        },
        animationSpec = TweenSpec(durationMillis = showTime, easing = LinearEasing),
    )
    return returnResult
    var currentState = remember { isShow }.value
    val transition = updateTransition(currentState)
    return transition.animateInt(
        transitionSpec = {
            tween(durationMillis = showTime, easing = LinearEasing)
        },
    ) { show ->
        if (show) {
            if (isRight2Left) {
                -(screenSize) / 2
            } else {
                (screenSize * 1.5).toInt()
            }
        } else {
            if (isRight2Left) {
                screenSize
            } else {
                -(screenSize) / 2
            }
        }
    }
}


@Composable
fun DanmakuText(
    modifier: Modifier, danmaku: DanmakuBean, curIndex: Int, all: List<DanmakuBean>, offset: Offset, screenSize: Size
) {
    val workState = danmaku.workState.value

    when (workState) {
        DanmkuAnimationState.IN_WORK -> {

            val transitionState = remember {
                MutableTransitionState(screenSize.width.toInt()).apply {
                    targetState = -screenSize.width.toInt()
                }
            }
//            DisposableEffect(Unit) {
//                onDispose {
//                    println(danmaku.body + "desssss")
//                    danmaku.workState.value = DanmkuAnimationState.OUT_WORK
//                }
//            }
            val transition = updateTransition(transitionState, label = "height transition")
            val width by transition.animateInt(transitionSpec = {
                tween(
                    durationMillis = DANMAKU_SPEED, easing = LinearEasing
                )
            }) {
                it
            }
            val color = if (danmaku.color == 0 || danmaku.color == 16777215) {
                Color.White
            } else {
                Color(0xFF000000 or danmaku.color.toLong())
            }
            val density = LocalDensity.current
            val fontResolver = LocalFontFamilyResolver.current
            val style = remember { TextStyle(fontSize = 26.sp) }
            LaunchedEffect(Unit) {
                danmaku.width = ParagraphIntrinsics(
                    text = danmaku.displayStr(),
                    style = style,
                    spanStyles = listOf(),
                    placeholders = listOf(),
                    density = density,
                    fontFamilyResolver = fontResolver
                ).minIntrinsicWidth.roundToInt()
            }
            Text(
                modifier = modifier.absoluteOffset {
                    if (width <= -screenSize.width.toInt() / 2) {
                        danmaku.workState.value = DanmkuAnimationState.OUT_WORK
                    }
                    var previousWidth = 0
                    for (findIndex in curIndex downTo 0) {
                        if (all[curIndex].showIndex == danmaku.showIndex) {
                            previousWidth = all[curIndex].width
//                            println("find width $previousWidth")
                            break
                        }
                    }
                    IntOffset(
                        x = width + previousWidth,
                        y = (((48.dp.value.toInt()) + (danmaku.showIndex * 82.dp).value) % (screenSize.height - (48 + 82 * 2 + 82 * 2).dp.value)).toInt()
                    )
                }, text = danmaku.displayStr(), color = color, style = TextStyle(
                    fontSize = 26.sp, shadow = Shadow(
                        color = if (color.value <= Color.Black.value) {
                            Color.White
                        } else {
                            Color.Black
                        }, offset = offset, blurRadius = 3f
                    )
                ), fontWeight = FontWeight.Medium
            )

        }

        DanmkuAnimationState.IN_ANIMATION -> {

        }

        DanmkuAnimationState.OUT_WORK -> {
//            LaunchedEffect(Unit) {
//                println(danmaku.body + "out work")
//            }
        }
    }

//    val color = if (danmaku.color == 0 || danmaku.color == 16777215) {
//        Color.White
//    } else {
//        Color(0xFF000000 or danmaku.color.toLong())
//    }
//    Text(
//        modifier = modifier.absoluteOffset {
////            if (xState.value <= (-(screenSize.width.toInt()) / 2)) {
//////                            danmaku.isShow.value = false
////            }
//            IntOffset(
//                x = screenSize.width.toInt(),
//                y = (((48.dp.value.toInt()) + (danmaku.showIndex * 82.dp).value) % (screenSize.height - (48 + 82 * 2 + 82 * 2).dp.value)).toInt()
//            )
//        }, text = danmaku.body.orEmpty(), color = color, style = TextStyle(
//            fontSize = 26.sp, shadow = Shadow(
//                color = if (color.value <= Color.Black.value) {
//                    Color.White
//                } else {
//                    Color.Black
//                }, offset = offset, blurRadius = 3f
//            )
//        ), fontWeight = FontWeight.Medium
//    )
}

fun getDanmakuType(
    type: Int,
//    viewportWidth: Float,
//    viewportHeight: Float
): BaseDanmaku {
    return when (type) {
        1 -> BaseDanmaku.R2LDanmaku
        4 -> BaseDanmaku.FBDanmaku
        5 -> BaseDanmaku.FTDanmaku
        6 -> BaseDanmaku.L2RDanmaku
        7 -> {
            BaseDanmaku.SpecialDanmaku
        }

        else -> {
            BaseDanmaku.R2LDanmaku
        }
    }
}

enum class BaseDanmaku {
    R2LDanmaku, FBDanmaku, FTDanmaku, L2RDanmaku, SpecialDanmaku
}

@Composable
fun MeasureUnconstrainedViewWidth(
    viewToMeasure: @Composable () -> Unit,
    content: @Composable (measuredWidth: Int) -> Unit,
) {
    SubcomposeLayout { constraints ->
        val measuredWidth = subcompose("viewToMeasure", viewToMeasure)[0].measure(Constraints()).width

        val contentPlaceable = subcompose("content") {
            content(measuredWidth)
        }[0].measure(constraints)
        layout(contentPlaceable.width, contentPlaceable.height) {
            contentPlaceable.place(0, 0)
        }
    }
}