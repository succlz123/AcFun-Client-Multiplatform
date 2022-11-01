package org.succlz123.app.acfun.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.tab.*
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.common.getPlatformName
import org.succlz123.lib.focus.onFocusKeyEventMove
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform
import org.succlz123.lib.screen.viewmodel.viewModel
import org.succlz123.lib.window.rememberIsWindowExpanded

@Composable
fun MainScreen() {
    val isExpandedScreen = rememberIsWindowExpanded()
    val homeVm = viewModel(MainViewModel::class) {
        MainViewModel()
    }
    val leftSelectItem = homeVm.leftSelectItem.collectAsState()

    val focusVm = viewModel(GlobalFocusViewModel::class) {
        GlobalFocusViewModel()
    }

    val leftFocus = homeVm.leftFocus
    val rightFocus = homeVm.rightFocus

    if (isExpandedScreen) {
        val curParentFocused = focusVm.curFocusRequesterParent.collectAsState()
        LaunchedEffect(Unit) {
            if (curParentFocused.value == null) {
                focusVm.curFocusRequesterParent.value = leftFocus
            }
        }
        Row(
            modifier = Modifier.fillMaxSize().background(Color.White), verticalAlignment = Alignment.CenterVertically
        ) {
            MainLeft(Modifier.fillMaxHeight().background(ColorResource.background)
                .onFocusKeyEventMove(leftCanMove = { false },
                    rightCanMove = {
                        rightFocus.requestFocus()
                        false
                    },
                    upCanMove = { leftSelectItem.value != 0 },
                    downCanMove = { leftSelectItem.value < MainViewModel.MAIN_TITLE.size - 1 },
                    backMove = {
                        if (getPlatformName() == "Android") {
                            System.exit(0)
                        }
                        false
                    }).focusRequester(leftFocus).onFocusChanged {
                    if (it.hasFocus) {
                        focusVm.curFocusRequesterParent.value = leftFocus
                    }
                    println("left parent has $it")
                }.focusTarget(),
                isExpandedScreen,
                { curParentFocused.value == leftFocus },
                leftFocus,
                rightFocus,
                { homeVm.leftSelectItem.value = it },
                { leftSelectItem.value })
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 18.dp, 0.dp, 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImageUrlMultiPlatform(
                        url = "ic_acfun_title.png", modifier = Modifier.width(83.dp).height(25.dp)
                    )
                }

                MainRight(
                    Modifier.background(Color.White).fillMaxHeight().weight(1f), isExpandedScreen, rightFocus, leftFocus
                ) { leftSelectItem.value }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().background(ColorResource.background)) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(0.dp, 18.dp, 0.dp, 8.dp), contentAlignment = Alignment.Center
            ) {
                AsyncImageUrlMultiPlatform(
                    url = "ic_acfun_title.png", modifier = Modifier.width(83.dp).height(25.dp)
                )
            }
            MainLeft(Modifier.fillMaxWidth(),
                isExpandedScreen,
                { false },
                leftFocus,
                rightFocus,
                { homeVm.leftSelectItem.value = it },
                { leftSelectItem.value })
            MainRight(
                Modifier.background(Color.White).fillMaxHeight().weight(1f), isExpandedScreen, rightFocus, leftFocus
            ) { leftSelectItem.value }
        }
    }
}

@Composable
fun MainLeft(
    modifier: Modifier,
    isExpandedScreen: Boolean,
    isParentFocused: () -> Boolean,
    parentFocusRequester: FocusRequester,
    otherFocusRequester: FocusRequester,
    changeLeftSelect: (Int) -> Unit,
    getLeftSelect: () -> Int
) {
    val scrollState = rememberLazyListState()
    if (isExpandedScreen) {
        LazyColumn(
            modifier = modifier.padding(12.dp), state = scrollState, verticalArrangement = Arrangement.Center
        ) {
            this.mainLeft(
                isParentFocused,
                parentFocusRequester,
                otherFocusRequester,
                changeLeftSelect,
                getLeftSelect,
                isExpandedScreen
            )
        }
    } else {
        LazyRow(
            modifier = modifier.padding(12.dp), state = scrollState, horizontalArrangement = Arrangement.Center
        ) {
            this.mainLeft(
                isParentFocused,
                parentFocusRequester,
                otherFocusRequester,
                changeLeftSelect,
                getLeftSelect,
                isExpandedScreen
            )
        }
    }
}

fun LazyListScope.mainLeft(
    parentHasFocus: () -> Boolean,
    parentFocusRequester: FocusRequester,
    otherFocusRequester: FocusRequester,
    changeLeftSelect: (Int) -> Unit,
    getLeftSelect: () -> Int,
    isExpandedScreen: Boolean
) {
    itemsIndexed(MainViewModel.MAIN_TITLE) { index, item ->
        val focusRequester = remember { FocusRequester() }
        val isFocused = remember { mutableStateOf(false) }
        if (parentHasFocus() && getLeftSelect() == index) {
            SideEffect {
                focusRequester.requestFocus()
            }
        }
        Card(modifier = Modifier.onFocusChanged {
            isFocused.value = it.isFocused
            if (isFocused.value) {
                changeLeftSelect(index)
            }
        }.focusRequester(focusRequester).focusProperties {
            right = otherFocusRequester
        }.focusTarget().noRippleClickable {
            parentFocusRequester.requestFocus()
            focusRequester.requestFocus()
        }, shape = RoundedCornerShape(8.dp), elevation = 0.dp, backgroundColor = if (isFocused.value) {
            ColorResource.acRed
        } else if (getLeftSelect() == index) {
            ColorResource.acRed30
        } else {
            Color.Transparent
        }
        ) {
            if (isExpandedScreen) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(12.dp, 8.dp)
                ) {
                    Icon(
                        MainViewModel.MAIN_ICON[index],
                        modifier = Modifier.size(20.dp),
                        contentDescription = item,
                        tint = if (getLeftSelect() == index) {
                            Color.White
                        } else {
                            Color.LightGray
                        }
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        item, style = MaterialTheme.typography.body1, color = if (getLeftSelect() == index) {
                            Color.White
                        } else {
                            Color.Gray
                        }
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp, 8.dp)
                ) {
                    Icon(
                        MainViewModel.MAIN_ICON[index],
                        modifier = Modifier.size(20.dp),
                        contentDescription = item,
                        tint = if (index == getLeftSelect()) {
                            Color.White
                        } else {
                            Color.LightGray
                        }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        item, style = MaterialTheme.typography.body1, color = if (index == getLeftSelect()) {
                            Color.White
                        } else {
                            Color.Gray
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun MainRight(
    modifier: Modifier,
    isExpandedScreen: Boolean,
    thisRequester: FocusRequester,
    otherRequester: FocusRequester,
    leftSelectItem: () -> Int
) {
    when (leftSelectItem()) {
        0 -> {
            MainHomeTab(modifier = modifier, isExpandedScreen, thisRequester, otherRequester)
        }

        1 -> {
            MainLiveTab(modifier = modifier, isExpandedScreen, thisRequester, otherRequester)
        }

        2 -> {
            MainRankTab(modifier = modifier, isExpandedScreen, thisRequester, otherRequester)
        }

        3 -> {
            MainSearchTab(modifier = modifier, isExpandedScreen)
        }

        4 -> {
            MainDownloadTab(modifier = modifier, isExpandedScreen)
        }

        5 -> {
            MainSettingTab(modifier = modifier, thisRequester, otherRequester)
        }

        else -> {
            Box(modifier = modifier)
        }
    }
}

