package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
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
    val rememberSelectedItem = homeVm.rightSelectedCategoryItem.collectAsState()
    Column(modifier = modifier.background(Color.White)) {
        showHomeTitle(change = { homeVm.rightSelectedCategoryItem.value = it }) {
            rememberSelectedItem.value
        }
        AcDivider()
        val recommendMap = homeVm.recommendMap.collectAsState()
        showHomeContent(
            isExpandedScreen,
            { homeVm.rightSelectedCategoryItem.value = it },
            { rememberSelectedItem.value },
            homeVm
        ) {
            if (recommendMap.value.isEmpty()) {
                // for Android Platform - java.lang.ClassCastException: kotlin.collections.EmptyMap cannot be cast to java.util.HashMap
                ScreenResult.Uninitialized
            } else {
                recommendMap.value[HomeAreaViewModel.CATEGORY[rememberSelectedItem.value].id]
                    ?: ScreenResult.Uninitialized
            }
        }
        LaunchedEffect(rememberSelectedItem.value) {
            homeVm.getData()
        }
    }
}

@Composable
fun showHomeTitle(change: (Int) -> Unit, cb: () -> Int) {
    Column(modifier = Modifier.padding(12.dp, 12.dp)) {
        LazyRow(
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(36.dp)
        ) {
            itemsIndexed(HomeAreaViewModel.CATEGORY) { index, item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.noRippleClickable {
                        change.invoke(index)
                    },
                ) {
                    Text(
                        text = item.name.orEmpty(),
                        style = MaterialTheme.typography.h3,
                        color = if (index == cb.invoke()) {
                            ColorResource.acRed
                        } else {
                            Color.Black
                        }
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Box(
                        modifier = Modifier.height(3.dp).width(22.dp).background(
                            if (index == cb.invoke()) {
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
}

@Composable
fun showHomeContent(
    isExpandedScreen: Boolean,
    changeTitleSelectIfExist: (Int) -> Unit,
    selectTitleIfExist: () -> Int,
    homeVm: HomeAreaViewModel,
    cb: () -> ScreenResult<ImmutableList<HomeRecommendItem>>
) {
    MainHomeContentItem(result = cb.invoke(),
        changeTitleSelectIfExist = changeTitleSelectIfExist,
        selectTitleIfExist = selectTitleIfExist,
        isExpandedScreen = isExpandedScreen,
        onRefresh = {
            homeVm.getData(true)
        })
}

