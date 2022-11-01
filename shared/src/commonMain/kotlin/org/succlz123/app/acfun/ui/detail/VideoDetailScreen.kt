package org.succlz123.app.acfun.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.VideoContent
import org.succlz123.app.acfun.base.AcBackButton
import org.succlz123.app.acfun.base.LoadingFailView
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.GlobalFocusViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.common.getPlatformName
import org.succlz123.lib.filedownloader.core.DownloadRequest
import org.succlz123.lib.filedownloader.core.DownloadStateType
import org.succlz123.lib.filedownloader.core.FileDownLoader
import org.succlz123.lib.focus.FocusNode
import org.succlz123.lib.focus.notAllowMove
import org.succlz123.lib.focus.onFocusKeyEventMove
import org.succlz123.lib.focus.onFocusParent
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.ScreenArgs
import org.succlz123.lib.screen.ext.popupwindow.PopupWindowLayout
import org.succlz123.lib.screen.operation.PushOptions
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.value
import org.succlz123.lib.screen.viewmodel.viewModel
import org.succlz123.lib.window.rememberIsWindowExpanded

@Composable
fun VideoDetailScreen() {
    val screenNavigation = LocalScreenNavigator.current
    val screenRecord = LocalScreenRecord.current
    val acContent = screenRecord.arguments.value<AcContent?>("KEY_AC_CONTENT")
    if (acContent == null) {
        screenNavigation.pop()
        return
    }
    val viewModel = viewModel {
        VideoDetailViewModel()
    }
    LaunchedEffect(Unit) {
        viewModel.getDetail(acContent)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White).noRippleClickable {
        screenNavigation.cancelPopupWindow()
    }) {
        val videoContent = viewModel.videoContentState.collectAsState().value
        when (videoContent) {
            is ScreenResult.Uninitialized, is ScreenResult.Loading -> {
                LoadingView()
            }

            is ScreenResult.Fail -> {
                LoadingFailView(cancelClick = {
                    screenNavigation.pop()
                }, okClick = {
                    viewModel.getDetail(acContent, true)
                })
            }

            is ScreenResult.Success -> {
                val vc = videoContent.invoke()
                Box(contentAlignment = Alignment.Center) {
                    videoDetailContent(
                        acContent, vc, viewModel, viewModel.userSpaceFocusParent, viewModel.episodeFocusParent
                    )

                    val showPlayerLoading = remember { mutableStateOf(false) }
                    if (showPlayerLoading.value) {
                        LoadingView()
                    }
                    LaunchedEffect(Unit) {
                        viewModel.playVideoContentEvent.collect {
                            when (it) {
                                is ScreenResult.Fail -> {
                                    screenNavigation.toast("视频播放地址加载失败，请点击重试。")
                                    showPlayerLoading.value = false
                                }

                                is ScreenResult.Loading -> {
                                    showPlayerLoading.value = true
                                }

                                is ScreenResult.Success -> {
                                    showPlayerLoading.value = false
                                    screenNavigation.push(
                                        Manifest.VideoPlayerScreen,
                                        arguments = ScreenArgs.putValue("KEY_VIDEO_CONTENT", it.invoke())
                                    )
                                }

                                ScreenResult.Uninitialized -> {
                                    showPlayerLoading.value = false
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun videoDetailContent(
    acContent: AcContent,
    vContent: VideoContent,
    viewModel: VideoDetailViewModel,
    userSpaceFocusParent: FocusRequester,
    episodeFocusParent: FocusRequester
) {
    val screenNavigation = LocalScreenNavigator.current
    Column(modifier = Modifier.fillMaxSize().padding(24.dp, 48.dp, 24.dp, 48.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AcBackButton()
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = viewModel.videoContentState.value.invoke()?.title.orEmpty(), style = MaterialTheme.typography.h1
            )
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Card(modifier = Modifier.size(42.dp), shape = RoundedCornerShape(42.dp)) {
                AsyncImageUrlMultiPlatform(
                    url = vContent.user?.headUrl.orEmpty(), modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            val isFocused = remember { mutableStateOf(false) }
            val userSpaceFocusNode = remember { FocusNode(tag = "UserSpace") }
            SideEffect {
                if (viewModel.currentFocusNode.value == userSpaceFocusNode) {
                    userSpaceFocusParent.requestFocus()
                }
            }
            Text(modifier = Modifier.onFocusParent(userSpaceFocusParent, "video detail - user space") {
                isFocused.value = (it.isFocused)
                if (it.isFocused) {
                    viewModel.currentFocusNode.value = userSpaceFocusNode
                }
            }.onFocusKeyEventMove(leftCanMove = notAllowMove,
                rightCanMove = notAllowMove,
                upCanMove = notAllowMove,
                downCanMove = {
                    viewModel.currentFocusNode.value = FocusNode(tag = "Episode")
                    episodeFocusParent.requestFocus()
                    false
                }).noRippleClickable {
                screenNavigation.push(
                    Manifest.UserSpaceScreen,
                    screenKey = vContent.user?.id.orEmpty(),
                    arguments = ScreenArgs.putValue("KEY_USER_NAME", vContent.user?.name)
                        .putValue("KEY_USER_ID", vContent.user?.id),
                    pushOptions = PushOptions(
                        removePredicate = PushOptions.RemoveAnyPredicate(Manifest.UserSpaceScreen)
                    )
                )
                userSpaceFocusParent.requestFocus()
            }.background(
                if (isFocused.value) {
                    ColorResource.acRed
                } else {
                    Color.Transparent
                }, shape = RoundedCornerShape(6.dp)
            ).padding(12.dp, 6.dp),
                text = vContent.user?.name.orEmpty(),
                fontSize = 20.sp,
                color = if (isFocused.value) {
                    Color.White
                } else {
                    ColorResource.acRed
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "发布于: " + vContent.createTime.orEmpty(),
                    style = MaterialTheme.typography.body2,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "关注数: " + vContent.user?.fanCount.orEmpty(),
                    style = MaterialTheme.typography.body2,
                    fontSize = 12.sp
                )
            }
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(16.dp))
        if (!vContent.description.isNullOrEmpty()) {
            Text(text = vContent.description, style = MaterialTheme.typography.body1, maxLines = 8)
        }
        Spacer(modifier = Modifier.fillMaxWidth().height(18.dp))
        Text(text = "分集： \n", style = MaterialTheme.typography.h5)

        LaunchedEffect(Unit) {
            viewModel.downloadVideoContentEvent.collect {
                when (it) {
                    is ScreenResult.Fail -> {
                        screenNavigation.toast("视频播放地址读取失败，无法下载，请点击重试。")
                    }

                    is ScreenResult.Loading -> {
                    }

                    is ScreenResult.Success -> {
                        val url =
                            it.invoke().playerList?.adaptationSet?.firstOrNull()?.representation?.firstOrNull()?.url
                        val downloadState = FileDownLoader.instance().get(
                            DownloadRequest(
                                id = it.invoke().videoList?.get(it.invoke().epIndex - 1)?.id
                                    ?: (it.invoke().title + it.invoke().epIndex),
                                url = url,
                                title = acContent.title + if ((vContent.videoList?.size ?: 0) > 1) {
                                    "_P${it.invoke().epIndex}"
                                } else {
                                    ""
                                },
                                image = acContent.img,
                                tag = acContent.up
                            )
                        )
                        when (downloadState.downloadStateType.value) {
                            DownloadStateType.Starting, DownloadStateType.Downloading -> {
                                screenNavigation.toast("已经在下载队列中...")
                            }

                            DownloadStateType.Finish -> {
                                screenNavigation.toast("视频已经下载成功, 请去下载页面查看")
                            }

                            DownloadStateType.Uninitialized, is DownloadStateType.Error, DownloadStateType.Pause -> {
                                screenNavigation.toast("添加下载任务到队列中")
                                FileDownLoader.instance().download(downloadState)
                            }
                        }
                    }

                    ScreenResult.Uninitialized -> {
                    }
                }
            }
        }
        val isExpandedScreen = rememberIsWindowExpanded()

        val focusVm = viewModel(GlobalFocusViewModel::class) {
            GlobalFocusViewModel()
        }
        LaunchedEffect(Unit) {
            if (focusVm.curFocusRequesterParent.value == null) {
                viewModel.episodeFocusParent.requestFocus()
            }
        }

        val grid = remember {
            if (isExpandedScreen) {
                6
            } else {
                2
            }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(grid),
            modifier = Modifier.fillMaxSize().onFocusParent(episodeFocusParent, "video detail - episode")
                .onFocusKeyEventMove(upCanMove = {
                    if (viewModel.currentFocusNode.value.index < grid) {
                        viewModel.currentFocusNode.value = FocusNode(tag = "UserSpace")
                        userSpaceFocusParent.requestFocus()
                    }
                    true
                }),
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(vContent.videoList.orEmpty()) { index, item ->

                val focusRequester = remember { FocusRequester() }
                val episodeFocusNode = remember { FocusNode(tag = "Episode", index = index) }

                val curParentFocused = focusVm.curFocusRequesterParent.collectAsState()
                if (curParentFocused.value == episodeFocusParent && (viewModel.currentFocusNode.value == episodeFocusNode)) {
                    SideEffect {
                        focusRequester.requestFocus()
                    }
                }

                val isFocused = viewModel.currentFocusNode.collectAsState().value == episodeFocusNode

                Box(
                    modifier = Modifier.weight(1f).height(52.dp).clip(MaterialTheme.shapes.medium).background(
                        if (isFocused) {
                            ColorResource.acRed
                        } else {
                            ColorResource.background
                        }
                    )
                ) {
                    Box(modifier = Modifier.align(Alignment.Center).fillMaxSize().padding(0.dp, 0.dp, 32.dp, 0.dp)
                        .focusRequester(focusRequester).onFocusChanged {
                            if (it.isFocused) {
                                viewModel.currentFocusNode.value = FocusNode(tag = "Episode", index = index)
                            }
                        }.focusTarget().noRippleClickable {
                            viewModel.play(acContent, index + 1)
                        }, contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.h3,
                            color = if (isFocused) {
                                ColorResource.white
                            } else {
                                ColorResource.black
                            }
                        )
                    }
                    PopupWindowLayout(modifier = Modifier.align(Alignment.CenterEnd), displayContent = {
                        Card(
                            modifier = Modifier.padding(12.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = 3.dp,
                            backgroundColor = Color.White
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                            ) {
                                OptionItem(modifier = Modifier.noRippleClickable {
                                    if (getPlatformName() == "Android") {
                                        screenNavigation.toast("Android 还未适配下载...")
                                        screenNavigation.cancelPopupWindow()
                                        return@noRippleClickable
                                    }
                                    viewModel.download(acContent, index + 1)
                                    screenNavigation.cancelPopupWindow()
                                }, "下载视频")
                                Spacer(modifier = Modifier.height(24.dp))
                                OptionItem(modifier = Modifier.noRippleClickable {
                                    screenNavigation.toast("开发中...")
                                    screenNavigation.cancelPopupWindow()
                                }, "无线投屏")
                            }
                        }
                    }, clickableContent = {
                        AsyncImageUrlMultiPlatform(
                            url = "ic_show_more.png",
                            modifier = Modifier.padding(12.dp, 6.dp, 12.dp, 6.dp).width(20.dp).height(20.dp)
                        )
                    })
                }
            }
        }
    }
}


@Composable
fun OptionItem(modifier: Modifier, title: String) {
    Text(modifier = modifier, text = title, fontSize = 16.sp, color = Color.Black)
}
