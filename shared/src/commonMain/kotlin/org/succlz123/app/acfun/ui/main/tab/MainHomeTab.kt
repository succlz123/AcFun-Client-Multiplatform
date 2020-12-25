package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.base.AcDivider
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.app.acfun.ui.main.vm.HomeAreaViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.viewmodel.viewModel

@Composable
fun MainHomeTab(modifier: Modifier, isExpandedScreen: Boolean) {
    val homeVm = viewModel {
        HomeAreaViewModel()
    }
    val rememberSelectedItem = remember { homeVm.rightSelectedCategoryItem }
    Column(modifier = modifier.background(Color.White)) {
        Column(modifier = Modifier.padding(12.dp, 12.dp)) {
            LazyRow(
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                itemsIndexed(HomeAreaViewModel.CATEGORY) { index, item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.noRippleClickable {
                            rememberSelectedItem.value = index
                        },
                    ) {
                        Text(
                            text = item.name.orEmpty(),
                            style = MaterialTheme.typography.h3,
                            color = if (index == rememberSelectedItem.value) {
                                ColorResource.acRed
                            } else {
                                Color.Black
                            }
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Box(
                            modifier = Modifier.height(3.dp).width(22.dp).background(
                                if (index == rememberSelectedItem.value) {
                                    ColorResource.acRed
                                } else {
                                    Color.Transparent
                                }
                            )
                        )
                    }
                }
            }
        }
        AcDivider()
        LaunchedEffect(rememberSelectedItem.value) {
            homeVm.getData()
        }
        val result =
            homeVm.recommendMap[HomeAreaViewModel.CATEGORY[rememberSelectedItem.value].id] ?: ScreenResult.Uninitialized
        MainHomeContentItem(result = result,
            isExpandedScreen = isExpandedScreen,
            rememberSelectedItem = rememberSelectedItem,
            onRefresh = {
                homeVm.getData(true)
            })
    }
}

