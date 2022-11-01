package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.GlobalFocusViewModel
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.app.acfun.ui.main.tab.item.MainRightTitleLayout
import org.succlz123.app.acfun.ui.main.vm.HomeRankViewModel
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.focus.onFocusKeyEventMove
import org.succlz123.lib.focus.onFocusParent
import org.succlz123.lib.screen.viewmodel.viewModel

@Composable
fun MainRankTab(
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean,
    thisRequester: FocusRequester,
    otherRequester: FocusRequester
) {
    val rankVm = viewModel {
        HomeRankViewModel()
    }
    val optionalState = rankVm.rankSelectIndex.collectAsState()

    val focusVm = viewModel(GlobalFocusViewModel::class) {
        GlobalFocusViewModel()
    }

    MainRightTitleLayout(modifier
        .onFocusParent(thisRequester).onFocusKeyEventMove(upCanMove = {
            true
        }, downCanMove = {
            true
        }, leftCanMove = {
            true
        }, rightCanMove = {
            true
        }, backMove = {
            otherRequester.requestFocus()
            true
        }), text = "全站排行", topRightContent = {
        val optional = rankVm.rankOption.keys.toList()
        LazyRow(modifier = Modifier) {
            itemsIndexed(optional) { index, item ->
                val focusRequester = remember { FocusRequester() }
                val isFocused = remember { mutableStateOf(false) }
                Text(
                    modifier = Modifier.padding(12.dp, 0.dp).noRippleClickable {
                        rankVm.rankSelectIndex.value = index
                    }, text = item, fontSize = 16.sp, color = if (optionalState.value == index) {
                        ColorResource.acRed
                    } else {
                        ColorResource.subText
                    }, fontWeight = FontWeight.Bold
                )
            }
        }
    }) {
        LaunchedEffect(Unit) {
            rankVm.getRankData(ArrayList(rankVm.rankOption.keys.toList())[optionalState.value])
            snapshotFlow { optionalState.value }.drop(1).distinctUntilChanged().collect {
                rankVm.getRankData(ArrayList(rankVm.rankOption.keys.toList())[it], isForce = true)
            }
        }
        MainHomeContentItem(Modifier,
            result = rankVm.rank.collectAsState().value,

            thisRequester = thisRequester,
            otherRequester = otherRequester,

            isExpandedScreen = isExpandedScreen,
            onRefresh = {
                rankVm.getRankData(ArrayList(rankVm.rankOption.keys.toList())[optionalState.value], isForce = true)
            })
    }
}
