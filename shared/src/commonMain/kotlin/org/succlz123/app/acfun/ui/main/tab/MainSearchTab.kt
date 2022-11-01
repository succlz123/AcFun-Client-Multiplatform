package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.app.acfun.ui.main.tab.item.MainRightTitleLayout
import org.succlz123.app.acfun.ui.main.vm.HomeSearchViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.screen.viewmodel.viewModel

@Composable
fun MainSearchTab(modifier: Modifier = Modifier, isExpandedScreen: Boolean) {
    val searchVm = viewModel {
        HomeSearchViewModel()
    }
    val inputText = searchVm.searchText.collectAsState()

    val focusRequester = remember { FocusRequester() }

    MainRightTitleLayout(modifier, text = "搜索", topRightContent = {
        Row {
            BasicTextField(
                modifier = Modifier.width(280.dp).background(ColorResource.background).padding(18.dp, 12.dp),
                textStyle = TextStyle(fontSize = 16.sp),
                maxLines = 1,
                value = inputText.value,
                onValueChange = { searchVm.searchText.value = it },
            )
            Spacer(modifier = Modifier.width(18.dp))
            Card(backgroundColor = Color.White, elevation = 6.dp) {
                Box(modifier = Modifier.padding(12.dp).noRippleClickable {
                    searchVm.search()
                }) {
                    Icon(
                        Icons.Sharp.Search,
                        modifier = Modifier.size(18.dp),
                        contentDescription = "Search",
                        tint = ColorResource.acRed
                    )
                }
            }
        }
    }) {
        MainHomeContentItem(
            result = searchVm.search.collectAsState().value,

            thisRequester = focusRequester,
            otherRequester = null,

            isExpandedScreen = isExpandedScreen,
            onRefresh = {
                searchVm.search()
            }) { searchVm.loadMore() }
    }
}
