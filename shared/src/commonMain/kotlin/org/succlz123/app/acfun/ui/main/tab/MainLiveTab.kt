package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.app.acfun.ui.main.tab.item.MainRightTitleLayout
import org.succlz123.app.acfun.ui.main.vm.HomeLiveViewModel
import org.succlz123.lib.screen.viewmodel.viewModel

@Composable
fun MainLiveTab(
    modifier: Modifier, isExpandedScreen: Boolean, thisRequester: FocusRequester, otherRequester: FocusRequester
) {
    val liveVm = viewModel {
        HomeLiveViewModel()
    }
    LaunchedEffect(Unit) {
        liveVm.getLiveRoomData()
    }

    MainRightTitleLayout(modifier, text = "直播") {
        MainHomeContentItem(result = liveVm.homeLiveRoomList.collectAsState().value,

            thisRequester = thisRequester,
            otherRequester = otherRequester,

            isExpandedScreen = isExpandedScreen,
            onRefresh = {
                liveVm.refreshLiveRoomData()
            }) {
            liveVm.loadMoreLiveRoomData()
        }
    }
}
