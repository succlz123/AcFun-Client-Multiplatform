package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.tab.item.MainRightTitleLayout
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.common.getPlatformName
import org.succlz123.lib.filedownloader.core.DownloadRequest
import org.succlz123.lib.filedownloader.core.DownloadState
import org.succlz123.lib.filedownloader.core.DownloadStateType
import org.succlz123.lib.filedownloader.core.FileDownLoader
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.ScreenArgs
import org.succlz123.lib.screen.ScreenNavigator

@Composable
fun MainDownloadTab(modifier: Modifier = Modifier, isExpandedScreen: Boolean) {
    MainRightTitleLayout(text = "下载") {
        Box(modifier = Modifier.padding(26.dp, 26.dp, 26.dp, 26.dp).fillMaxSize()) {
            if (getPlatformName() == "Android") {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Android 还未适配下载")
                }
            } else {
                val allLocal: MutableList<DownloadRequest> = remember {
                    mutableStateListOf<DownloadRequest>().apply {
                        addAll(FileDownLoader.instance().getAllDownloadFile().orEmpty().toMutableStateList())
                    }
                }
                LazyColumn {
                    itemsIndexed(allLocal, key = { index, item ->
                        item.toString()
                    }) { index, item ->
                        DownloadItem(
                            item.image.orEmpty(), item.title.orEmpty(), item.tag.orEmpty(), item, isExpandedScreen
                        ) {
                            allLocal.remove(it)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DownloadItem(
    image: String,
    title: String,
    up: String,
    downloadRequest: DownloadRequest,
    isExpandedScreen: Boolean,
    cb: (DownloadRequest) -> Unit
) {
    val screenNavigator = LocalScreenNavigator.current

    Column(
        modifier = Modifier.background(Color.White).clip(MaterialTheme.shapes.medium)
            .border(1.dp, ColorResource.border, MaterialTheme.shapes.medium).fillMaxWidth()
    ) {
        val downloadState = remember {
            FileDownLoader.instance().get(downloadRequest)
        }
        val downloadStateType = downloadState.downloadStateType.collectAsState()
        val progress = downloadState.progress.collectAsState()

        Row(
            modifier = Modifier.padding(0.dp, 0.dp, 24.dp, 0.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            Box(modifier = Modifier.height(80.dp).width(150.dp)) {
                AsyncImageUrlMultiPlatform(
                    url = image, modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    modifier = if (isExpandedScreen) {
                        Modifier.width(320.dp)
                    } else {
                        Modifier
                    }, text = title, maxLines = 2, style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(up, style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(6.dp))
            }
            if (isExpandedScreen) {
                Spacer(modifier = Modifier.width(24.dp))
                ProgressView(
                    Modifier.weight(1f).height(3.dp),
                    progress,
                    downloadStateType,
                    downloadState,
                    downloadRequest,
                    screenNavigator,
                    cb
                )
            }
        }
        if (!isExpandedScreen) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.padding(0.dp, 0.dp, 24.dp, 0.dp), verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(24.dp))
                ProgressView(
                    Modifier.weight(1f).height(3.dp),
                    progress,
                    downloadStateType,
                    downloadState,
                    downloadRequest,
                    screenNavigator,
                    cb
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProgressView(
    modifier: Modifier = Modifier,
    progress: State<Int>,
    downloadStateType: State<DownloadStateType>,
    downloadState: DownloadState,
    downloadRequest: DownloadRequest,
    screenNavigator: ScreenNavigator,
    cb: (DownloadRequest) -> Unit
) {
    LinearProgressIndicator(
        color = ColorResource.acRed,
        backgroundColor = Color.LightGray,
        modifier = modifier,
        progress = progress.value / 100f
    )
    Spacer(modifier = Modifier.width(24.dp))
    Text(
        text = "${progress.value}%", maxLines = 1, style = MaterialTheme.typography.body1
    )
    Spacer(modifier = Modifier.width(24.dp))
    Text(
        text = when (downloadStateType.value) {
            DownloadStateType.Starting, DownloadStateType.Downloading -> {
                "暂停"
            }

            DownloadStateType.Uninitialized, is DownloadStateType.Error, DownloadStateType.Pause -> {
                "继续"
            }

            DownloadStateType.Finish -> {
                "播放"
            }
        }, modifier = Modifier.noRippleClickable {
            when (downloadStateType.value) {
                DownloadStateType.Starting, DownloadStateType.Downloading -> {
                    FileDownLoader.instance().pause(downloadState)
                }

                DownloadStateType.Uninitialized, is DownloadStateType.Error, DownloadStateType.Pause -> {
                    FileDownLoader.instance().download(downloadState)
                }

                DownloadStateType.Finish -> {
                    screenNavigator.push(
                        Manifest.VideoPlayerScreen,
                        arguments = ScreenArgs.putValue("KEY_VIDEO_LOCAL_TITLE", downloadRequest.title).putValue(
                            "KEY_VIDEO_LOCAL_FILE", downloadRequest.m3u8FilePath
                        )
                    )
                }
            }
        }, maxLines = 1, style = MaterialTheme.typography.body1
    )
    Spacer(modifier = Modifier.width(24.dp))
    Text(text = "取消", maxLines = 1, style = MaterialTheme.typography.body1, modifier = Modifier.noRippleClickable {
        FileDownLoader.instance().stop(downloadState)
        cb.invoke(downloadRequest)
    })
}