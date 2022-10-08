package org.succlz123.app.acfun.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.tab.*
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform
import org.succlz123.lib.screen.viewmodel.viewModel
import org.succlz123.lib.window.rememberIsWindowExpanded

@Composable
fun MainScreen(modifier: Modifier) {
    val isExpandedScreen = rememberIsWindowExpanded()
    val homeVm = viewModel(MainViewModel::class) {
        MainViewModel()
    }
    val leftSelectItem = homeVm.leftSelectItem.collectAsState()
    if (isExpandedScreen) {
        Row(
            modifier = modifier.background(Color.White), verticalAlignment = Alignment.CenterVertically
        ) {
            MainLeft(Modifier.fillMaxHeight().background(ColorResource.background),
                isExpandedScreen,
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
                    Modifier.background(Color.White).fillMaxHeight().weight(1f), isExpandedScreen
                ) { leftSelectItem.value }
            }
        }
    } else {
        Column(modifier = modifier.background(ColorResource.background)) {
            Box(
                modifier = Modifier.fillMaxWidth().padding(0.dp, 18.dp, 0.dp, 8.dp), contentAlignment = Alignment.Center
            ) {
                AsyncImageUrlMultiPlatform(
                    url = "ic_acfun_title.png", modifier = Modifier.width(83.dp).height(25.dp)
                )
            }
            MainLeft(Modifier.fillMaxWidth(),
                isExpandedScreen,
                { homeVm.leftSelectItem.value = it },
                { leftSelectItem.value })
            MainRight(
                Modifier.background(Color.White).fillMaxHeight().weight(1f), isExpandedScreen
            ) { leftSelectItem.value }
        }
    }
}

@Composable
fun MainLeft(modifier: Modifier, isExpandedScreen: Boolean, changeLeftSelect: (Int) -> Unit, getLeftSelect: () -> Int) {
    val scrollState = rememberLazyListState()
    if (isExpandedScreen) {
        LazyColumn(
            modifier = modifier.padding(12.dp), state = scrollState, verticalArrangement = Arrangement.Center
        ) {
            this.mainLeft(changeLeftSelect, getLeftSelect, isExpandedScreen)
        }
    } else {
        LazyRow(
            modifier = modifier.padding(12.dp), state = scrollState, horizontalArrangement = Arrangement.Center
        ) {
            this.mainLeft(changeLeftSelect, getLeftSelect, isExpandedScreen)
        }
    }
}

fun LazyListScope.mainLeft(
    changeLeftSelect: (Int) -> Unit, getLeftSelect: () -> Int, isExpandedScreen: Boolean
) {
    itemsIndexed(MainViewModel.MAIN_TITLE) { index, item ->
        Card(
            modifier = Modifier.noRippleClickable {
                changeLeftSelect(index)
            }, shape = RoundedCornerShape(8.dp), elevation = 0.dp, backgroundColor = if (index == getLeftSelect()) {
                ColorResource.acRed
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
                        tint = if (index == getLeftSelect()) {
                            Color.White
                        } else {
                            Color.LightGray
                        }
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        item, style = MaterialTheme.typography.body1, color = if (index == getLeftSelect()) {
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
fun MainRight(modifier: Modifier, isExpandedScreen: Boolean, leftSelectItem: () -> Int) {
    when (leftSelectItem()) {
        0 -> {
            MainHomeTab(modifier = modifier, isExpandedScreen)
        }

        1 -> {
            MainLiveTab(modifier = modifier, isExpandedScreen)
        }

        2 -> {
            MainRankTab(modifier = modifier, isExpandedScreen)
        }

        3 -> {
            MainSearchTab(modifier = modifier, isExpandedScreen)
        }

        4 -> {
            MainDownloadTab(modifier = modifier, isExpandedScreen)
        }

        5 -> {
            MainSettingTab(modifier = modifier)
        }

        else -> {
            Box(modifier = modifier)
        }
    }
}

