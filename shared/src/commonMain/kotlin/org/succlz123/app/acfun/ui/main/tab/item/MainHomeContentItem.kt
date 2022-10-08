package org.succlz123.app.acfun.ui.main.tab.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.base.AcGo2TopButton
import org.succlz123.app.acfun.base.AcRefreshButton
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.ui.main.vm.HomeAreaViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.ScreenArgs
import org.succlz123.lib.screen.operation.PushOptions
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.scroll.VerticalScrollbar
import org.succlz123.lib.scroll.rememberGridScrollContext
import org.succlz123.lib.scroll.rememberVerticalScrollbarAdapter

@Composable
fun MainHomeContentItem(
    modifier: Modifier = Modifier,
    result: ScreenResult<ArrayList<HomeRecommendItem>>,
    changeTitleSelectIfExist: (Int) -> Unit = {},
    selectTitleIfExist: () -> Int = { 0 },
    isExpandedScreen: Boolean = true,
    onRefresh: () -> Unit,
    onLoadMore: (() -> Unit)? = null
) {
    val content = remember(result) {
        result.invoke()
    }
    if (content != null) {
        MainHomeContentItemSuccess(
            modifier, content, changeTitleSelectIfExist, selectTitleIfExist, isExpandedScreen, onRefresh, onLoadMore
        )
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
    changeTitleSelectIfExist: (Int) -> Unit,
    selectTitleIfExist: () -> Int,
    isExpandedScreen: Boolean,
    refreshClick: () -> Unit,
    onLoadMore: (() -> Unit)?
) {
    val screenNavigator = LocalScreenNavigator.current
    val gridCount = if (isExpandedScreen) {
        4
    } else {
        2
    }
    val scrollState = rememberLazyGridState()
    val scrollContext = rememberGridScrollContext(scrollState)
    Box(modifier = modifier) {
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            adapter = rememberVerticalScrollbarAdapter(scrollState, GridCells.Fixed(gridCount), null)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(gridCount),
            modifier = Modifier.fillMaxSize().padding(24.dp, 0.dp),
            state = scrollState,
            contentPadding = PaddingValues(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(content, key = { _, item ->
                item.toString()
            }, span = { _: Int, item: HomeRecommendItem ->
                GridItemSpan(
                    if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                        gridCount
                    } else {
                        1
                    }
                )
            }, contentType = { _: Int, item: HomeRecommendItem ->
                item.viewType
            }) { index, item ->
                if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                    MainHomeContentTitle(modifier = Modifier.noRippleClickable {
                        if (selectTitleIfExist() == 0) {
                            val find = HomeAreaViewModel.CATEGORY.find { it.id == item.titleId }
                            if (find != null) {
                                changeTitleSelectIfExist(HomeAreaViewModel.CATEGORY.indexOf(find))
                            }
                        } else {
                            screenNavigator.push(
                                Manifest.AreaContentScreen,
                                screenKey = item.titleId.toString(),
                                arguments = ScreenArgs.putValue("KEY_CATEGORY_NAME", item.titleStr.orEmpty())
                                    .putValue("KEY_CATEGORY_ID", item.titleId),
                                pushOptions = PushOptions(
                                    removePredicate = PushOptions.RemoveAnyPredicate(Manifest.UserSpaceScreen)
                                )
                            )
                        }
                    }, item.titleStr.orEmpty())
                } else {
                    MainHomeContentInfo(item)
                }
            }
        }
        Column(Modifier.padding(48.dp).align(Alignment.BottomEnd)) {
            AnimatedVisibility(!scrollContext.isTop) {
                val cs = rememberCoroutineScope()
                AcGo2TopButton(Modifier) {
                    cs.launch {
                        scrollState.animateScrollToItem(index = 0)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            AcRefreshButton(Modifier, refreshClick)
        }
        if (scrollContext.isBottom) {
            onLoadMore?.invoke()
        }
    }
}