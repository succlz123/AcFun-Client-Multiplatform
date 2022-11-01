package org.succlz123.app.acfun.ui.main.tab.item

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.succlz123.app.acfun.Manifest
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.base.AcGo2TopButton
import org.succlz123.app.acfun.base.AcRefreshButton
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.ui.main.GlobalFocusViewModel
import org.succlz123.app.acfun.ui.main.vm.HomeAreaViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.focus.onFocusKeyEventMove
import org.succlz123.lib.focus.onFocusParent
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.LocalScreenWindowSizeHolder
import org.succlz123.lib.screen.ScreenArgs
import org.succlz123.lib.screen.lifecycle.ScreenLifecycle
import org.succlz123.lib.screen.operation.PushOptions
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.viewmodel.viewModel
import org.succlz123.lib.scroll.VerticalScrollbar
import org.succlz123.lib.scroll.rememberGridScrollContext
import org.succlz123.lib.scroll.rememberVerticalScrollbarAdapter
import org.succlz123.lib.vm.BaseViewModel

class MainContentViewModel : BaseViewModel() {

    var focusIndex = MutableStateFlow(0)
}

@Composable
fun MainHomeContentItem(
    modifier: Modifier = Modifier, result: ScreenResult<ImmutableList<HomeRecommendItem>>,

    thisRequester: FocusRequester, otherRequester: FocusRequester? = null,

    changeTitleSelectIfExist: (Int) -> Unit = {}, selectTitleIfExist: () -> Int = { 0 },

    isExpandedScreen: Boolean = true,

    onRefresh: () -> Unit, onLoadMore: (() -> Unit)? = null
) {
    val content = remember(result) {
        result.invoke()
    }
    if (content != null) {
        MainHomeContentItemSuccess(
            modifier,
            content,
            thisRequester,
            otherRequester,
            changeTitleSelectIfExist,
            selectTitleIfExist,
            isExpandedScreen,
            onRefresh,
            onLoadMore
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
    modifier: Modifier, content: ImmutableList<HomeRecommendItem>,

    thisRequester: FocusRequester, otherRequester: FocusRequester? = null,

    changeTitleSelectIfExist: (Int) -> Unit, selectTitleIfExist: () -> Int,

    isExpandedScreen: Boolean,

    refreshClick: () -> Unit, onLoadMore: (() -> Unit)?
) {
    val screenNavigator = LocalScreenNavigator.current
    val gridCount = if (isExpandedScreen) {
        4
    } else {
        2
    }

    val vm = viewModel(MainContentViewModel::class, key = thisRequester.toString()) { MainContentViewModel() }
    val focusVm = viewModel(GlobalFocusViewModel::class) { GlobalFocusViewModel() }
    val focusIndex = vm.focusIndex.collectAsState().value
    val focusManager = LocalFocusManager.current

    val screenRecord = LocalScreenRecord.current

    val scrollState = rememberLazyGridState()
    val scrollContext = rememberGridScrollContext(scrollState)
    LaunchedEffect(Unit) {
        focusVm.curFocusRequesterParent.collect {
            if (screenRecord.hostLifecycle.getCurrentState() == ScreenLifecycle.State.RESUMED) {
                if (it != thisRequester) {
                    vm.focusIndex.value = 0
                    scrollState.scrollToItem(0)
                } else {
                    it.requestFocus()
                }
            }
        }
    }
    println("content item ${focusIndex}")
    Box(
        modifier = modifier.onFocusParent(thisRequester, "home item").onFocusKeyEventMove(upCanMove = {
            var hasTitleItem: HomeRecommendItem? = null
            var checkVideoItem: HomeRecommendItem? = null
            for (check in (focusIndex - gridCount) until focusIndex) {
                if (check >= 0) {
                    val innerCheckItem = content[check]
                    if (innerCheckItem.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                        hasTitleItem = innerCheckItem
                        break
                    } else {
                        if (checkVideoItem == null) {
                            checkVideoItem = innerCheckItem
                        }
                    }
                }
            }
            if (hasTitleItem != null) {
                focusVm.curFocusRequesterParent.value = thisRequester
                vm.focusIndex.value = content.indexOf(hasTitleItem)
            } else if (checkVideoItem != null) {
                focusVm.curFocusRequesterParent.value = thisRequester
                vm.focusIndex.value = content.indexOf(checkVideoItem)
            } else {
                otherRequester?.requestFocus()
            }
            false
        }, rightCanMove = {
            val item = content[focusIndex]
            if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                false
            } else if (item.innerItemCount % gridCount == (gridCount - 1)) {
                false
            } else {
                vm.focusIndex.value = vm.focusIndex.value + 1
                focusVm.curFocusRequesterParent.value = thisRequester
                false
            }
        }, leftCanMove = {
            val item = content[focusIndex]
            if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                false
            } else if (item.innerItemCount % gridCount == 0) {
                focusManager.moveFocus(FocusDirection.Left)
                false
            } else {
                vm.focusIndex.value = vm.focusIndex.value - 1
                focusVm.curFocusRequesterParent.value = thisRequester
                false
            }
        }, downCanMove = {
            val item = content[focusIndex]
            if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                if (focusIndex + 1 < content.size) {
                    vm.focusIndex.value = vm.focusIndex.value + 1
                    focusVm.curFocusRequesterParent.value = thisRequester
                }
            } else {
                var hasTitleItem: HomeRecommendItem? = null
                var checkVideoItem: HomeRecommendItem? = null
                for (check in (focusIndex + gridCount) downTo focusIndex) {
                    if (check < content.size) {
                        val innerCheckItem = content[check]
                        if (innerCheckItem.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                            hasTitleItem = innerCheckItem
                            break
                        } else {
                            if (checkVideoItem == null) {
                                checkVideoItem = innerCheckItem
                            }
                        }
                    }
                }
                if (hasTitleItem != null) {
                    vm.focusIndex.value = content.indexOf(hasTitleItem)
                    focusVm.curFocusRequesterParent.value = thisRequester
                } else if (checkVideoItem != null) {
                    vm.focusIndex.value = content.indexOf(checkVideoItem)
                    focusVm.curFocusRequesterParent.value = thisRequester
                }
            }
            false
        }, backMove = {
            if (otherRequester == null) {
                false
            } else {
                otherRequester.requestFocus()
                true
            }
        })
    ) {
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

                val focusRequester = remember { FocusRequester() }
                val curParentFocused = focusVm.curFocusRequesterParent.collectAsState()
                val isFocused = curParentFocused.value == thisRequester && (focusIndex == index)
                if (isFocused) {
                    SideEffect {
                        focusRequester.requestFocus()
                    }
                }
                val sizeHolder = LocalScreenWindowSizeHolder.current
                val offset = remember { (sizeHolder.size.value.width / 4 / 1.82f).toInt() }
                val scope = rememberCoroutineScope()

                if (item.viewType == HomeRecommendItem.VIEW_TYPE_TITLE) {
                    MainHomeContentTitle(modifier = Modifier.focusRequester(focusRequester).onFocusChanged {
                        if (it.isFocused) {
                            scope.launch {
                                scrollState.animateScrollToItem(index, -offset)
                            }
                        }
                    }.focusTarget().noRippleClickable {
                        if (selectTitleIfExist() == 0) {
                            val find = HomeAreaViewModel.CATEGORY.find { it.id == item.titleId }
                            if (find != null) {
                                changeTitleSelectIfExist(HomeAreaViewModel.CATEGORY.indexOf(find))
                                vm.focusIndex.value = 0
                                focusRequester.requestFocus()
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
                            vm.focusIndex.value = index
                            focusVm.curFocusRequesterParent.value = thisRequester
                            focusRequester.requestFocus()
                        }
                    }, item.titleStr.orEmpty()
                    ) { isFocused }
                } else {
                    MainHomeContentInfo(Modifier.noRippleClickable {
                        if (item.item?.type == AcContent.TYPE_LIVE) {
                            screenNavigator.push(
                                Manifest.LiveStreamPlayerScreen,
                                screenKey = item.item?.url,
                                arguments = ScreenArgs.putValue("KEY_ID", item.item?.url)
                                    .putValue("KEY_TITLE", item.item?.title)
                            )
                        } else if (item.item?.type == AcContent.TYPE_VIDEO) {
                            screenNavigator.push(
                                Manifest.VideoDetailScreen,
                                screenKey = item.item?.url,
                                arguments = ScreenArgs.putValue("KEY_AC_CONTENT", item.item)
                            )
                        }
                        vm.focusIndex.value = index
                        focusVm.curFocusRequesterParent.value = thisRequester
                        focusRequester.requestFocus()
                    }.focusRequester(focusRequester).onFocusChanged {
                        if (it.isFocused) {
                            scope.launch {
                                scrollState.animateScrollToItem(index, -offset)
                            }
                        }
                    }.focusTarget(), item) { isFocused }
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