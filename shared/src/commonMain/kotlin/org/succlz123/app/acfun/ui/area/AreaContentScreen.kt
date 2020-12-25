package org.succlz123.app.acfun.ui.area

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import org.succlz123.app.acfun.base.AcBackButton
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.value
import org.succlz123.lib.screen.viewmodel.viewModel
import org.succlz123.lib.window.rememberIsWindowExpanded

@Composable
fun AreaContentScreen() {
    val navigationScene = LocalScreenNavigator.current
    val screenRecord = LocalScreenRecord.current
    val name = screenRecord.arguments.value<String>("KEY_CATEGORY_NAME")
    val id = screenRecord.arguments.value<Int>("KEY_CATEGORY_ID")
    if (name.isNullOrEmpty() || id == null) {
        navigationScene.pop()
        return
    }
    val isExpandedScreen = rememberIsWindowExpanded()

    val viewModel = viewModel(key = id.toString()) {
        AreaContentViewModel()
    }
    LaunchedEffect(key1 = id) {
        viewModel.getData(id)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        val state = viewModel.areaVideosState.value
        when (state) {
            is ScreenResult.Uninitialized -> {
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
                        Button(colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White,
                            disabledContentColor = Color.Transparent
                        ), contentPadding = PaddingValues(
                            start = 32.dp, top = 10.dp, end = 32.dp, bottom = 10.dp
                        ), onClick = {
                            navigationScene.pop()
                        }) {
                            Text(
                                text = "退出", fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(32.dp))
                        Button(colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.Black,
                            contentColor = Color.White,
                            disabledContentColor = Color.Transparent
                        ), contentPadding = PaddingValues(
                            start = 32.dp, top = 10.dp, end = 32.dp, bottom = 10.dp
                        ), onClick = {
                            viewModel.getData(id)
                        }) {
                            Text(
                                text = "重试", fontSize = 18.sp
                            )
                        }
                    }
                }
            }

            is ScreenResult.Success, is ScreenResult.Loading -> {
                val acContentList = state.invoke()
                Column(
                    modifier = Modifier.fillMaxSize().padding(0.dp, 48.dp, 0.dp, 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp, 0.dp, 24.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AcBackButton()
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = name, style = MaterialTheme.typography.h1)
                        Spacer(modifier = Modifier.weight(1f))

                        val optional = AreaContentViewModel.MAP.keys.toList()
                        LazyRow(modifier = Modifier) {
                            itemsIndexed(optional) { index, item ->
                                Text(
                                    modifier = Modifier.padding(12.dp, 0.dp).noRippleClickable {
                                        viewModel.rankSelectIndex.value = index
                                    },
                                    text = item,
                                    fontSize = 16.sp,
                                    color = if (viewModel.rankSelectIndex.value == index) {
                                        ColorResource.acRed
                                    } else {
                                        ColorResource.subText
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    LaunchedEffect(Unit) {
                        viewModel.getData(
                            id,
                            ArrayList(AreaContentViewModel.MAP.keys.toList())[viewModel.rankSelectIndex.value]
                        )
                        snapshotFlow { viewModel.rankSelectIndex.value }.distinctUntilChanged().collect {
                            viewModel.getData(
                                id,
                                ArrayList(AreaContentViewModel.MAP.keys.toList())[viewModel.rankSelectIndex.value],
                                force = true
                            )
                        }
                    }
                    if (acContentList.isNullOrEmpty()) {
                        LoadingView()
                    } else {
                        val content = ArrayList(acContentList)
                        MainHomeContentItem(
                            result = ScreenResult.Success(content),
                            rememberSelectedItem = null,
                            isExpandedScreen = isExpandedScreen,
                            onRefresh = {
                                viewModel.getData(
                                    id,
                                    ArrayList(AreaContentViewModel.MAP.keys.toList())[viewModel.rankSelectIndex.value],
                                    force = true
                                )
                            },
                            onLoadMore = {
                                viewModel.getData(id)
                            })
                    }
                }
            }
        }
    }
}