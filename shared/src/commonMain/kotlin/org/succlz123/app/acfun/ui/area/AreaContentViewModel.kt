package org.succlz123.app.acfun.ui.area

import androidx.compose.ui.focus.FocusRequester
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.seimicrawler.xpath.JXDocument
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.vm.ScreenPageViewModel
import org.succlz123.lib.network.HttpX
import org.succlz123.lib.result.screenResultDataNone
import org.succlz123.lib.screen.result.ScreenResult

class AreaContentViewModel : ScreenPageViewModel() {

    companion object {
        // 最多
        const val rankScore = "综合"
        const val createTime = "最新"
        const val viewCount = "播放"
        const val commentCount = "评论"
        const val bananaCount = "投蕉"
        const val danmakuCount = "弹幕"

        val MAP = mapOf(
            rankScore to "rankScore",
            createTime to "createTime",
            viewCount to "viewCount",
            commentCount to "commentCount",
            bananaCount to "bananaCount",
            danmakuCount to "danmakuCount"
        )
    }

    val areaVideosState = MutableStateFlow<ScreenResult<ImmutableList<HomeRecommendItem>>>(ScreenResult.Uninitialized)

    var defaultSor = rankScore

    val rankSelectIndex: MutableStateFlow<Int> = MutableStateFlow(0)

    val contentFocusParent = FocusRequester()

    init {
        page = 1
    }

    // https://www.acfun.cn/v/list106/index.htm
    // https://www.acfun.cn/v/list106/index.htm?sortField=rankScore&duration=all&date=default&page=334
    fun getData(id: Int, sort: String = rankScore, force: Boolean = false) {
        if (areaVideosState.value is ScreenResult.Loading) {
            return
        }
        if (force) {
            hasMore = true
            page = 1
        }
        if (!hasMore) {
            return
        }
        if (force) {
            areaVideosState.value = ScreenResult.Loading()
        } else {
            areaVideosState.value = ScreenResult.Loading(areaVideosState.value.invoke())
        }
        getInnerData(id, sort)
    }

    private fun getInnerData(id: Int, sort: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = getFromNetwork(id, sort)
                var cur = ArrayList(areaVideosState.value.invoke().orEmpty())
                if (data.isNullOrEmpty()) {
                    if (cur.isEmpty()) {
                        areaVideosState.value = screenResultDataNone()
                    } else {
                        hasMore = false
                    }
                } else {
                    cur.addAll(data)
                    page++
                    areaVideosState.value = ScreenResult.Success(cur.toImmutableList())
                }
            } catch (e: Exception) {
                areaVideosState.value = ScreenResult.Fail(e)
            }
        }
    }

    private fun getFromNetwork(id: Int, sort: String): ArrayList<HomeRecommendItem>? {
        val doc =
            HttpX.doGet("https://www.acfun.cn/v/list${id}/index.htm?sortField=${MAP[sort]}&duration=all&date=default&page=${page}")
        if (doc.isNullOrEmpty()) {
            return null
        }

        val jxDocument = JXDocument.create(doc)
        val list = ArrayList<HomeRecommendItem>()
        val areas = jxDocument.selN("//div[@class='list-content-item']")
        for (area in areas) {
            val ele = area.asElement()
            val titleClass = ele.getElementsByClass("list-content-title")
            var title: String? = null
            var url: String? = null
            var up: String? = null
            var img: String? = null
            if (titleClass.isNotEmpty()) {
                title = titleClass[0].getElementsByTag("a").text()
                url = titleClass[0].getElementsByTag("a").attr("href")
            }
            val upClass = ele.getElementsByClass("list-content-uplink")
            if (upClass.isNotEmpty()) {
                up = upClass[0].getElementsByTag("a").attr("title")
            }
            val imgClass = ele.getElementsByClass("list-content-cover")
            if (upClass.isNotEmpty()) {
                img = imgClass[0].attr("src")
            }
            val body = AcContent()
            body.title = title
            body.up = up
            body.img = img
            body.time = ele.getElementsByClass("video-time").text()
            body.url = "https://www.acfun.cn$url"
            val videoItem = HomeRecommendItem()
            videoItem.item = body
            list.add(videoItem)
        }
        return list
    }
}
