package org.succlz123.app.acfun.ui.main.vm

import androidx.compose.ui.focus.FocusRequester
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import org.jsoup.select.Elements
import org.seimicrawler.xpath.JXDocument
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.api.bean.RecommendHomePageletList
import org.succlz123.app.acfun.category.CategoryManager
import org.succlz123.lib.network.HttpX
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.vm.BaseViewModel

class HomeAreaViewModel : BaseViewModel() {

    companion object {
        val CATEGORY = CategoryManager.getHomeCategory().children
    }

    val rightSelectedCategoryItem = MutableStateFlow(0)

    val recommendMap = MutableStateFlow(emptyMap<Int, ScreenResult<ImmutableList<HomeRecommendItem>>>())

    val homeContentFocusRequester = FocusRequester()

    fun getData(isForce: Boolean = false) {
        if (rightSelectedCategoryItem.value == 0) {
            getHomeData(isForce)
        } else {
            getAreaData(CATEGORY[rightSelectedCategoryItem.value].id, isForce)
        }
    }

    private fun getHomeData(isForce: Boolean = false) {
        if (recommendMap.value[-1] is ScreenResult.Loading) {
            return
        }
        if (!isForce && recommendMap.value[-1] is ScreenResult.Success) {
            return
        }
        recommendMap.value += hashMapOf(-1 to ScreenResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getHomeFromNetwork()
                if (response.isNullOrEmpty()) {
                    recommendMap.value += hashMapOf(-1 to ScreenResult.Fail(NullPointerException()))
                } else {
                    recommendMap.value += hashMapOf(-1 to ScreenResult.Success(response.toImmutableList()))
                }
            } catch (e: Exception) {
                recommendMap.value += hashMapOf(-1 to ScreenResult.Fail(e))
                e.printStackTrace()
            }
        }
    }

    private fun getHomeFromNetwork(): ArrayList<HomeRecommendItem>? {
        val doc =
            HttpX.doGet("https://www.acfun.cn/?pagelets=pagelet_game,pagelet_douga,pagelet_bangumi_list,pagelet_life,pagelet_tech,pagelet_dance,pagelet_music,pagelet_film,pagelet_fishpond,pagelet_sport&reqID=0&ajaxpipe=1")
        if (doc.isNullOrEmpty()) {
            return null
        }
        val pageList = doc.split("/*<!-- fetch-stream -->*/")
        val sortList = ArrayList<SortData>()
        for (s in pageList) {
            if (s.isEmpty()) {
                continue
            }
            val json = AcfunApiService.appJson.decodeFromString<RecommendHomePageletList>(s)
            if (json.html.isNullOrEmpty()) {
                continue
            }
            val jxDocument = JXDocument.create(json.html)
            val areas = jxDocument.selN("//div[@class='module-left']")
            for (area in areas) {
                val ele = area.asElement()
                var areaInfo: Elements? = null
                try {
                    areaInfo = ele.getElementsByClass("left-area").first().getElementsByTag("a")
                } catch (e: Exception) {
                }
                var idInt = -1
                var titleStr: String? = null
                if (areaInfo == null) {
                    val id = ele.child(1).attr("class").replace("video-list-", "")
                    if (id.isNotEmpty()) {
                        // wrong
                        idInt = id.toInt()
                        titleStr = CategoryManager.MAP[idInt]
                    }
                } else {
                    try {
                        idInt = areaInfo.attr("href").replace("/v/list", "").replace("/index.htm", "").toInt()
                    } catch (e: Exception) {
                    }
                    titleStr = ele.getElementsByClass("left-area").first().getElementsByClass("header-title").text()
                }

                val titleItem = HomeRecommendItem()
                titleItem.viewType = HomeRecommendItem.VIEW_TYPE_TITLE
                titleItem.titleId = idInt
                titleItem.titleStr = titleStr

                val sortData = SortData(titleItem)
                sortList.add(sortData)

                val videos = ele.getElementsByClass("normal-video-container")
                var count = 0
                for (video in videos) {
                    val img = video.getElementsByTag("img").attr("src")
                    video.getElementsByClass("normal-video-title").let {
                        val title = it.attr("title")
                        val body = AcContent()
                        val titleSplit = title.split("\r")
                        if (titleSplit.size == 3) {
                            body.title = titleSplit.first()
                            body.up = titleSplit[1]
                            body.info = titleSplit[2]
                        }
                        body.img = img
                        body.time = video.getElementsByClass("video-time").text()
                        body.url = "https://www.acfun.cn" + it.attr("href")
                        val videoItem = HomeRecommendItem()
                        videoItem.viewType = HomeRecommendItem.VIEW_TYPE_CARD
                        videoItem.titleStr = titleStr
                        videoItem.item = body
                        videoItem.innerItemCount = count
                        sortData.content.add(videoItem)
                        count++
                    }
                    if (count >= 8) {
                        break
                    }
                }
            }
        }
        sortList.sortBy {
            CategoryManager.PRIORITY_MAP[it.title.titleId]
        }
        val list = ArrayList<HomeRecommendItem>()
        for (sortData in sortList) {
            list.add(sortData.title)
            list.addAll(sortData.content)
        }
        return list
    }

    class SortData(var title: HomeRecommendItem) {
        val content = ArrayList<HomeRecommendItem>()
    }

    fun getAreaData(categoryId: Int, isForce: Boolean = false) {
        if (recommendMap.value[categoryId] is ScreenResult.Loading) {
            return
        }
        if (!isForce && recommendMap.value[categoryId] is ScreenResult.Success) {
            return
        }
        recommendMap.value += hashMapOf(categoryId to ScreenResult.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getAreaFromNetwork(categoryId)
                if (response.isNullOrEmpty()) {
                    recommendMap.value += hashMapOf(categoryId to ScreenResult.Fail(NullPointerException()))
                } else {
                    recommendMap.value += hashMapOf(categoryId to ScreenResult.Success(response.toImmutableList()))
                }
            } catch (e: Exception) {
                recommendMap.value += hashMapOf(categoryId to ScreenResult.Fail(e))
            }
        }
    }

    private fun getAreaFromNetwork(categoryId: Int): ArrayList<HomeRecommendItem>? {
        val doc = HttpX.doGet("https://www.acfun.cn/v/list${categoryId}/index.htm")
        if (doc.isNullOrEmpty()) {
            return null
        }
        val jxDocument = JXDocument.create(doc)
        val list = ArrayList<HomeRecommendItem>()
        val areas = jxDocument.selN("//div[@class='channel-channel-module']")
        try {
            for (area in areas) {
                val ele = area.asElement()
                var mid: String =
                    ele.getElementsByClass("more").firstOrNull()?.attr("href").orEmpty().removeSuffix("/index.htm")
                if (mid.startsWith("v/list")) {
                    mid = mid.removePrefix("v/list")
                } else if (mid.startsWith("/v/list")) {
                    mid = mid.removePrefix("/v/list")
                }
                if (mid.isNotEmpty()) {
                    val titleItem = HomeRecommendItem()
                    titleItem.viewType = HomeRecommendItem.VIEW_TYPE_TITLE
                    val midInt = mid.toIntOrNull() ?: continue
                    titleItem.titleId = midInt
                    titleItem.titleStr = CategoryManager.SUB_MAP[midInt]
                    list.add(titleItem)
                } else {
                    continue
                }
                val dates = ele.getElementsByClass("video-item")
                var count = 0
                for (date in dates) {
                    val attr = date.getElementsByClass("module-title").firstOrNull()?.tagName("title")?.children()
                        ?.firstOrNull()?.children()?.firstOrNull()?.attributes()
                    val url = attr?.get("href")
                    val title = attr?.get("title")
                    if (url.isNullOrEmpty() || title.isNullOrEmpty()) {
                        continue
                    }
                    val body = AcContent()
                    val line = title.split("\n")
                    body.title = line.firstOrNull()
                    body.up = if (line.size > 1) line[1] else null
                    body.img = date.getElementsByTag("img").attr("src")
                    body.time = if (line.size > 2) line[2].removePrefix("发布于").split("/").firstOrNull() else null
                    body.url = "https://www.acfun.cn$url"
                    val videoItem = HomeRecommendItem()
                    videoItem.viewType = HomeRecommendItem.VIEW_TYPE_CARD
                    videoItem.item = body
                    videoItem.innerItemCount = count
                    list.add(videoItem)
                    count++
                    if (count >= 8) {
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.toString()
        }
        return list
    }
}
