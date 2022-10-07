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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
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
    if (isExpandedScreen) {
        Row(
            modifier = modifier.background(Color.White), verticalAlignment = Alignment.CenterVertically
        ) {
            MainLeft(Modifier.fillMaxHeight().background(ColorResource.background), isExpandedScreen, homeVm)
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 18.dp, 0.dp, 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImageUrlMultiPlatform(
                        url = "ic_acfun_title.png", modifier = Modifier.width(83.dp).height(25.dp)
                    )
                }
                val rightModifier = remember {
                    Modifier.fillMaxHeight().weight(1f)
                }
                when (homeVm.leftSelectItem.value) {
                    0 -> {
                        MainHomeTab(modifier = rightModifier, isExpandedScreen)
                    }

                    1 -> {
                        MainLiveTab(modifier = rightModifier, isExpandedScreen)
                    }

                    2 -> {
                        MainRankTab(modifier = rightModifier, isExpandedScreen)
                    }

                    3 -> {
                        MainSearchTab(modifier = rightModifier, isExpandedScreen)
                    }

                    4 -> {
                        MainDownloadTab(modifier = rightModifier, isExpandedScreen)
                    }

                    5 -> {
                        MainSettingTab(modifier = rightModifier)
                    }

                    else -> {
                        Box(modifier = Modifier.background(Color.White).fillMaxHeight().weight(1f))
                    }
                }
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
            MainLeft(Modifier.fillMaxWidth(), isExpandedScreen, homeVm)
            val rightModifier = Modifier.fillMaxHeight().weight(1f)
            when (homeVm.leftSelectItem.value) {
                0 -> {
                    MainHomeTab(modifier = rightModifier, isExpandedScreen)
                }

                1 -> {
                    MainLiveTab(modifier = rightModifier, isExpandedScreen)
                }

                2 -> {
                    MainRankTab(modifier = rightModifier, isExpandedScreen)
                }

                3 -> {
                    MainSearchTab(modifier = rightModifier, isExpandedScreen)
                }

                4 -> {
                    MainDownloadTab(modifier = rightModifier, isExpandedScreen)
                }

                5 -> {
                    MainSettingTab(modifier = rightModifier)
                }

                else -> {
                    Box(modifier = Modifier.background(Color.White).fillMaxHeight().weight(1f))
                }
            }
        }
    }
}

@Composable
fun MainLeft(modifier: Modifier, isExpandedScreen: Boolean, mainVm: MainViewModel) {
    val scrollState = rememberLazyListState()
    val leftSelectItem = remember { mainVm.leftSelectItem }
    if (isExpandedScreen) {
        LazyColumn(
            modifier = modifier.padding(12.dp), state = scrollState, verticalArrangement = Arrangement.Center
        ) {
            this.mainLeft(mainVm, leftSelectItem, isExpandedScreen)
        }
    } else {
        LazyRow(
            modifier = modifier.padding(12.dp), state = scrollState, horizontalArrangement = Arrangement.Center
        ) {
            this.mainLeft(mainVm, leftSelectItem, isExpandedScreen)
        }
    }
}

fun LazyListScope.mainLeft(mainVm: MainViewModel, leftSelectItem: MutableState<Int>, isExpandedScreen: Boolean) {
    itemsIndexed(mainVm.leftTitle) { index, item ->
        Card(
            modifier = Modifier.noRippleClickable {
                leftSelectItem.value = index
            }, shape = RoundedCornerShape(8.dp), elevation = if (index == leftSelectItem.value) {
                3.dp
            } else {
                0.dp
            }, backgroundColor = if (index == leftSelectItem.value) {
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
                        mainVm.leftIcon[index],
                        modifier = Modifier.size(20.dp),
                        contentDescription = item,
                        tint = if (index == leftSelectItem.value) {
                            Color.White
                        } else {
                            Color.LightGray
                        }
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        item, style = MaterialTheme.typography.body1, color = if (index == leftSelectItem.value) {
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
                        mainVm.leftIcon[index],
                        modifier = Modifier.size(20.dp),
                        contentDescription = item,
                        tint = if (index == leftSelectItem.value) {
                            Color.White
                        } else {
                            Color.LightGray
                        }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        item, style = MaterialTheme.typography.body1, color = if (index == leftSelectItem.value) {
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

