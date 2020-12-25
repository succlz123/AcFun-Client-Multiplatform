package org.succlz123.app.acfun.ui.main.tab.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.base.AcGo2TopButton
import org.succlz123.app.acfun.base.AcRefreshButton
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.ScreenArgs
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.scroll.VerticalScrollbar
import org.succlz123.lib.scroll.rememberGridScrollContext
import org.succlz123.lib.scroll.rememberVerticalScrollbarAdapter

@Composable
fun MainHomeContentItem(
    modifier: Modifier = Modifier,
    result: ScreenResult<ArrayList<HomeRecommendItem>>,
    rememberSelectedItem: MutableState<Int>?,
    onRefresh: () -> Unit,
    isExpandedScreen: Boolean = true,
    onLoadMore: (() -> Unit)? = null
) {
    val content = result.invoke()
    if (content != null) {
        MainHomeContentItemSuccess(modifier, content, rememberSelectedItem, onRefresh, isExpandedScreen, onLoadMore)
    } else {
        when (result) {
            is ScreenResult.Fail -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "加载失败，请重试", style = MaterialTheme.typography.h3)
                    Spacer(modifier = Modifier.height(32.dp))
                    AcRefreshButton(Modifier, onRefresh)
                }
            }

            is ScreenResult.Uninitialized -> {}
            else -> {
                LoadingView()
            }
        }

    }
}

@Composable
private fun MainHomeContentItemSuccess(
    modifier: Modifier,
    content: ArrayList<HomeRecommendItem>,
    rememberSelectedItem: MutableState<Int>?,
    onClick: () -> Unit,
    isExpandedScreen: Boolean,
    onLoadMore: (() -> Unit)?
) {
    val navigationScene = LocalScreenNavigator.current
    val gridCount = if (isExpandedScreen) {
        4
    } else {
        2
    }
    val state = rememberLazyGridState()
    val scrollContext = rememberGridScrollContext(state)
    Box(modifier = modifier) {
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberVerticalScrollbarAdapter(state, GridCells.Fixed(gridCount), null)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridCount),
            modifier = Modifier.fillMaxSize().padding(24.dp, 0.dp),
            state = state,
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(content, key = { index, item ->
                item.toString()
            }, span = { index: Int, item: HomeRecommendItem ->
                GridItemSpan(
                    if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                        gridCount
                    } else {
                        1
                    }
                )
            }) { index, item ->
                if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                    MainHomeContentTitle(navigationScene, rememberSelectedItem, item.titleId, item.titleStr.orEmpty())
                } else {
                    Box(
                        modifier = Modifier.background(Color.White).clip(MaterialTheme.shapes.medium)
                            .border(1.dp, ColorResource.border, MaterialTheme.shapes.medium).fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.noRippleClickable {
                            if (item.item?.type == AcContent.TYPE_LIVE) {
                                navigationScene.push(
                                    Manifest.LiveStreamPlayerScreen,
                                    screenKey = item.item?.url,
                                    arguments = ScreenArgs.putValue("KEY_ID", item.item?.url)
                                        .putValue("KEY_TITLE", item.item?.title)
                                )
                            } else if (item.item?.type == AcContent.TYPE_VIDEO) {
                                navigationScene.push(
                                    Manifest.VideoDetailScreen,
                                    screenKey = item.item?.url,
                                    arguments = ScreenArgs.putValue("KEY_AC_CONTENT", item.item)
                                )
                            }
                        }.fillMaxSize()) {
                            Box(
                                modifier = Modifier.fillMaxWidth().aspectRatio(1.82f)
                                    .background(ColorResource.background), contentAlignment = Alignment.BottomEnd
                            ) {
                                AsyncImageUrlMultiPlatform(
                                    url = item.item?.img.orEmpty(), modifier = Modifier.fillMaxSize()
                                )
                                if (item.item?.view != null && item.item?.type == AcContent.TYPE_LIVE) {
                                    Text(
                                        modifier = Modifier.padding(6.dp)
                                            .background(ColorResource.black60, RoundedCornerShape(4.dp)).padding(6.dp),
                                        text = "在线：${item.item?.view.toString()}",
                                        maxLines = 1,
                                        color = Color.White,
                                        style = MaterialTheme.typography.overline
                                    )
                                }
                            }
                            Text(
                                modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 0.dp),
                                text = item.item?.title.orEmpty() + "\n",
                                maxLines = 2,
                                style = MaterialTheme.typography.h6
                            )
                            Row {
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    modifier = Modifier.padding(12.dp, 6.dp),
                                    text = item.item?.up.orEmpty(),
                                    color = ColorResource.subText,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.body2
                                )
                            }
                        }
                    }
                }
            }
        }
        Column(Modifier.padding(48.dp).align(Alignment.BottomEnd)) {
            AnimatedVisibility(!scrollContext.isTop) {
                val cs = rememberCoroutineScope()
                AcGo2TopButton(Modifier) {
                    cs.launch {
                        state.animateScrollToItem(index = 0)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AcRefreshButton(Modifier, onClick)
        }
        if (scrollContext.isBottom) {
            onLoadMore?.invoke()
        }
    }
}