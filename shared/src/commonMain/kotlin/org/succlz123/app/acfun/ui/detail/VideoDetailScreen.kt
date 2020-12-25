package org.succlz123.app.acfun.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults.outlinedButtonColors
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.VideoContent
import org.succlz123.app.acfun.base.AcBackButton
import org.succlz123.app.acfun.base.AcOutlineButton
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.common.getPlatformName
import org.succlz123.lib.filedownloader.core.DownloadRequest
import org.succlz123.lib.filedownloader.core.DownloadStateType
import org.succlz123.lib.filedownloader.core.FileDownLoader
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.ScreenArgs
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
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Box(modifier = Modifier.fillMaxSize().alpha(0.1f)) {
            AsyncImageUrlMultiPlatform(
                modifier = Modifier.fillMaxSize(), url = acContent.img.orEmpty(), contentScale = ContentScale.Crop
            )
        }
        val videoContent = viewModel.videoContentState.value

        when (videoContent) {
            is ScreenResult.Uninitialized, is ScreenResult.Loading -> {
                LoadingView()
            }

            is ScreenResult.Fail -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "数据加载失败!", fontSize = 32.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                    Row {
                        Button(colors = outlinedButtonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White,
                            disabledContentColor = Color.Transparent
                        ), contentPadding = PaddingValues(
                            start = 32.dp, top = 10.dp, end = 32.dp, bottom = 10.dp
                        ), onClick = {
                            screenNavigation.pop()
                        }) {
                            Text(
                                text = "退出", fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(32.dp))
                        Button(colors = outlinedButtonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White,
                            disabledContentColor = Color.Transparent
                        ), contentPadding = PaddingValues(
                            start = 32.dp, top = 10.dp, end = 32.dp, bottom = 10.dp
                        ), onClick = {
                            viewModel.getDetail(acContent, true)
                        }) {
                            Text(
                                text = "重试", fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            is ScreenResult.Success -> {
                val vc = videoContent.invoke()
                Box(contentAlignment = Alignment.Center) {
                    videoDetailContent(acContent, vc, viewModel)

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
fun videoDetailContent(acContent: AcContent, vContent: VideoContent, viewModel: VideoDetailViewModel) {
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
            Text(
                modifier = Modifier.noRippleClickable {
                    screenNavigation.push(
                        Manifest.UserSpaceScreen,
                        screenKey = vContent.user?.id.orEmpty(),
                        arguments = ScreenArgs.putValue("KEY_USER_NAME", vContent.user?.name)
                            .putValue("KEY_USER_ID", vContent.user?.id),
                        pushOptions = PushOptions(
                            removePredicate = PushOptions.RemoveAnyPredicate(Manifest.UserSpaceScreen)
                        )
                    )
                }, text = vContent.user?.name.orEmpty(), fontSize = 20.sp, color = ColorResource.acRed
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "发布于: " + vContent.createTime.orEmpty(),
                style = MaterialTheme.typography.body2,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "关注数: " + vContent.user?.fanCount.orEmpty(),
                style = MaterialTheme.typography.body2,
                fontSize = 12.sp
            )
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
                                url = url, title = acContent.title + if ((vContent.videoList?.size ?: 0) > 1) {
                                    "_P${it.invoke().epIndex}"
                                } else {
                                    ""
                                }, image = acContent.img, tag = acContent.up
                            )
                        )
                        when (downloadState.downloadStateType.value) {
                            DownloadStateType.Starting, DownloadStateType.Downloading -> {
                                screenNavigation.toast("下载中...")
                            }

                            DownloadStateType.Finish -> {
                                screenNavigation.toast("视频已经下载成功")
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(
                if (isExpandedScreen) {
                    8
                } else {
                    3
                }
            ), modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(vContent.videoList.orEmpty()) { index, item ->
                val modifier = if (index % 10 == 0) {
                    Modifier.padding(0.dp, 0.dp, 6.dp, 12.dp)
                } else if ((index + 1) % 12 == 0) {
                    Modifier.padding(6.dp, 0.dp, 0.dp, 12.dp)
                } else {
                    Modifier.padding(6.dp, 0.dp, 6.dp, 12.dp)
                }
                Row(
                    modifier = modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    AcOutlineButton(modifier = Modifier, onClick = {
                        viewModel.play(acContent, index + 1)
                    }, content = {
                        AsyncImageUrlMultiPlatform(url = "ic_download.png",
                            modifier = Modifier.width(20.dp).height(20.dp).noRippleClickable {
                                if (getPlatformName() == "Android") {
                                    screenNavigation.toast("Android 还未适配下载...")
                                    return@noRippleClickable
                                }
                                viewModel.download(acContent, index + 1)
                            })
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = (index + 1).toString(), fontSize = 16.sp)
                    })
                }
            }
        }
    }
}

