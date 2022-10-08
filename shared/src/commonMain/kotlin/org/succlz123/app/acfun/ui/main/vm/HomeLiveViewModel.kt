package org.succlz123.app.acfun.ui.main.vm

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.vm.ScreenPageViewModel
import org.succlz123.lib.result.screenResultDataNone
import org.succlz123.lib.screen.result.ScreenResult

class HomeLiveViewModel : ScreenPageViewModel() {

    val homeLiveRoomList =
        MutableStateFlow<ScreenResult<ImmutableList<HomeRecommendItem>>>(ScreenResult.Uninitialized)

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
                val cur = ArrayList(homeLiveRoomList.value.invoke().orEmpty())
                if (response.isNullOrEmpty()) {
                    if (cur.isEmpty()) {
                        homeLiveRoomList.value = screenResultDataNone()
                    } else {
                        hasMore = false
                    }
                } else {
                    if (response.size < 20) {
                        hasMore = false
                    }
                    cur.addAll(response)
                    homeLiveRoomList.value = ScreenResult.Success(cur.toImmutableList())
                    page++
                }
            } catch (e: Exception) {
                homeLiveRoomList.value = ScreenResult.Fail(e)
            }
        }
    }
}
