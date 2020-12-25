package org.succlz123.app.acfun.ui.main.vm

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.vm.ScreenPageViewModel
import org.succlz123.lib.result.screenResultDataNone
import org.succlz123.lib.screen.result.ScreenResult

class HomeLiveViewModel : ScreenPageViewModel() {

    val homeLiveRoomList = mutableStateOf<ScreenResult<ArrayList<HomeRecommendItem>>>(ScreenResult.Uninitialized)

    fun getLiveRoomData() {
        if (homeLiveRoomList.value !is ScreenResult.Success) {
            refreshLiveRoomData()
        }
    }

    fun refreshLiveRoomData() {
        homeLiveRoomList.value = ScreenResult.Uninitialized
        resetPageSize()
        loadMoreLiveRoomData()
    }

    fun loadMoreLiveRoomData() {
        if (homeLiveRoomList.value is ScreenResult.Loading) {
            return
        }
        if (!hasMore) {
            return
        }
        homeLiveRoomList.value = ScreenResult.Loading(homeLiveRoomList.value.invoke())
        getLiveRoomList()
    }

    private fun getLiveRoomList() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = AcfunApiService.getLiveRoomListFromNetwork(page, pageSize)
                val cur = homeLiveRoomList.value.invoke()
                if (response.isNullOrEmpty()) {
                    if (cur.isNullOrEmpty()) {
                        homeLiveRoomList.value = screenResultDataNone()
                    } else {
                        hasMore = false
                    }
                } else {
                    if (response.size < 20) {
                        hasMore = false
                    }
                    cur?.addAll(response)
                    homeLiveRoomList.value = ScreenResult.Success(cur ?: response)
                    page++
                }
            } catch (e: Exception) {
                homeLiveRoomList.value = ScreenResult.Fail(e)
            }
        }
    }
}
