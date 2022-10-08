package org.succlz123.app.acfun.ui.main.vm

import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.api.bean.RankList
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.vm.BaseViewModel

class HomeRankViewModel : BaseViewModel() {

    val rank = MutableStateFlow<ScreenResult<ImmutableList<HomeRecommendItem>>>(ScreenResult.Uninitialized)

    val rankOption = mapOf("今日" to "DAY", "三天" to "THREE_DAYS", "本周" to "WEEK")

    val rankSelectIndex = MutableStateFlow(0)

    fun getRankData(key: String = "今日", isForce: Boolean = false) {
        fetch(rank, isForce, true) {
            getRankFromNetwork(rankOption[key].orEmpty())?.toImmutableList()
        }
    }

    private suspend fun getRankFromNetwork(range: String = "DAY"): ArrayList<HomeRecommendItem>? {
        val result = AcfunApiService.httpClient.get("https://www.acfun.cn/rest/pc-direct/rank/channel") {
            parameter("rankPeriod", range)
        }.body<RankList>().rankList
        if (result.isNullOrEmpty()) {
            return null
        }

        var count = 0
        val list = ArrayList<HomeRecommendItem>()

        result.forEach {
            val body = AcContent()
            body.title = it.contentTitle
            body.up = it.userName
            body.info = it.description
            body.img = it.videoCover
            body.url = "https://www.acfun.cn/v/ac" + it.contentId
            val videoItem = HomeRecommendItem()
            videoItem.viewType = HomeRecommendItem.VIEW_TYPE_CARD
            videoItem.item = body
            videoItem.innerItemCount = count
            count++
            list.add(videoItem)
        }
        return list
    }
}
