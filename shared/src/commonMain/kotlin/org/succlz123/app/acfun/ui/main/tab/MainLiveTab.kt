package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.app.acfun.ui.main.tab.item.MainRightTitleLayout
import org.succlz123.app.acfun.ui.main.vm.HomeLiveViewModel
import org.succlz123.lib.screen.viewmodel.viewModel

@Composable
fun MainLiveTab(modifier: Modifier, isExpandedScreen: Boolean) {
    val liveVm = viewModel {
        HomeLiveViewModel()
    }
    LaunchedEffect(Unit) {
        liveVm.getLiveRoomData()
    }
    MainRightTitleLayout(modifier, text = "直播") {
        MainHomeContentItem(
            result = liveVm.homeLiveRoomList.value,
            rememberSelectedItem = null,
            isExpandedScreen = isExpandedScreen,
            onRefresh = {
                liveVm.refreshLiveRoomData()
            }
        ) {
            liveVm.loadMoreLiveRoomData()
        }
    }
}
