package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.base.AcDivider
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.GlobalFocusViewModel
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.app.acfun.ui.main.vm.HomeAreaViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.focus.onFocusKeyEventMove
import org.succlz123.lib.focus.onFocusParent
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.viewmodel.viewModel

@Composable
fun MainHomeTab(
    modifier: Modifier, isExpandedScreen: Boolean, thisRequester: FocusRequester, otherRequester: FocusRequester
) {
    val homeVm = viewModel {
        HomeAreaViewModel()
    }
    val selectedItem = homeVm.rightSelectedCategoryItem.collectAsState()

    val focusVm = viewModel(GlobalFocusViewModel::class) {
        GlobalFocusViewModel()
    }

    val curParentFocused = focusVm.curFocusRequesterParent.collectAsState()

    Column(modifier = modifier.background(Color.White).onFocusParent(thisRequester, "home", null)) {
        showHomeTitle(Modifier.onFocusKeyEventMove(upCanMove = { false },
            rightCanMove = { selectedItem.value < HomeAreaViewModel.CATEGORY.size - 1 },
            leftCanMove = {
                if (selectedItem.value == 0) {
                    otherRequester.requestFocus()
                }
                true
            },
            downCanMove = {
                true
            }, backMove = {
                otherRequester.requestFocus()
                true
            }),
            otherRequester,
            { curParentFocused.value == thisRequester },
            { homeVm.rightSelectedCategoryItem.value = it }) {
            selectedItem.value
        }
        AcDivider()
        val recommendMap = homeVm.recommendMap.collectAsState()
        showHomeContent(homeVm.homeContentFocusRequester, thisRequester,

            isExpandedScreen, { homeVm.rightSelectedCategoryItem.value = it }, { selectedItem.value }, homeVm
        ) {
            if (recommendMap.value.isEmpty()) {
                // for Android Platform - java.lang.ClassCastException: kotlin.collections.EmptyMap cannot be cast to java.util.HashMap
                ScreenResult.Uninitialized
            } else {
                recommendMap.value[HomeAreaViewModel.CATEGORY[selectedItem.value].id] ?: ScreenResult.Uninitialized
            }
        }
        LaunchedEffect(selectedItem.value) {
            homeVm.getData()
        }
    }
}

@Composable
fun showHomeTitle(
    modifier: Modifier, otherRequester: FocusRequester, hasFocus: () -> Boolean, change: (Int) -> Unit, cb: () -> Int
) {
    Column(modifier = modifier.padding(12.dp, 12.dp)) {
        LazyRow(
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            itemsIndexed(HomeAreaViewModel.CATEGORY) { index, item ->
                val focusRequester = remember { FocusRequester() }
                val isFocused = remember { mutableStateOf(false) }

                if (hasFocus() && cb() == index) {
                    SideEffect {
                        focusRequester.requestFocus()
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.onFocusChanged {
                        isFocused.value = it.isFocused
                        if (isFocused.value) {
                            change(index)
                        }
                        if (it.isFocused) {
                            println("home tab isFocused")
                        }
                    }.focusRequester(focusRequester).focusProperties {
                        if (index == 0) {
                            left = otherRequester
                        }
                    }.focusTarget().noRippleClickable {
                        focusRequester.requestFocus()
                    },
                ) {
                    Text(
                        text = item.name.orEmpty(), style = MaterialTheme.typography.h3, color = if (isFocused.value) {
                            ColorResource.acRed
                        } else if (cb() == index) {
                            ColorResource.acRed30
                        } else {
                            Color.Black
                        }
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier.height(3.dp).width(22.dp).background(
                            if (isFocused.value) {
                                ColorResource.acRed
                            } else if (cb() == index) {
                                ColorResource.acRed30
                            } else {
                                Color.Transparent
                            }
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun showHomeContent(
    thisRequester: FocusRequester,
    otherRequester: FocusRequester? = null,

    isExpandedScreen: Boolean,
    changeTitleSelectIfExist: (Int) -> Unit,
    selectTitleIfExist: () -> Int,
    homeVm: HomeAreaViewModel,
    cb: () -> ScreenResult<ImmutableList<HomeRecommendItem>>
) {
    MainHomeContentItem(result = cb.invoke(),

        thisRequester = thisRequester,
        otherRequester = otherRequester,

        changeTitleSelectIfExist = changeTitleSelectIfExist,
        selectTitleIfExist = selectTitleIfExist,
        isExpandedScreen = isExpandedScreen,
        onRefresh = {
            homeVm.getData(true)
        })
}

