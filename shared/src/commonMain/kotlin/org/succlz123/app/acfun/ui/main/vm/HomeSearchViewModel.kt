package org.succlz123.app.acfun.ui.main.vm

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.decodeFromString
import org.seimicrawler.xpath.JXDocument
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.api.bean.html.HtmlContent
import org.succlz123.app.acfun.vm.ScreenPageViewModel
import org.succlz123.lib.network.HttpX
import org.succlz123.lib.screen.result.ScreenResult
import kotlin.collections.set

class HomeSearchViewModel : ScreenPageViewModel() {
    val searchText = MutableStateFlow("")
    val search = MutableStateFlow<ScreenResult<ImmutableList<HomeRecommendItem>>>(ScreenResult.Uninitialized)

    fun search() {
        if (searchText.value.isEmpty()) {
            return
        }
        page = 0
        hasMore = true
        fetch(search, true, true) {
            getSearchData(java.net.URLEncoder.encode(searchText.value, "utf-8"))?.toImmutableList()
        }
    }

    fun loadMore() {
        if (!hasMore) {
            return
        }
        fetch(search, true, false) {
            getSearchData(java.net.URLEncoder.encode(searchText.value, "utf-8"))?.toImmutableList()
        }
    }

    // https://www.acfun.cn/search?type=video&keyword=${searchText}&pagelets=pagelet_header&reqID=0&ajaxpipe=1&pCursor=1
    private suspend fun getSearchData(searchText: String): ArrayList<HomeRecommendItem>? {
        val doc = HttpX.doGet("https://www.acfun.cn/search?type=video&keyword=${searchText}&pCursor=${page + 1}")
        if (doc.isNullOrEmpty()) {
            return null
        }
        val startStr1 = "\"text/javascript\">bigPipe.onPageletArrive("
        val startStr2 = "{\"container\":\"\",\"id\":\"pagelet_video\","
        val startStr = startStr1 + startStr2
        val endString = ");</script>"
        val startIndex = doc.indexOf(startStr)
        var newDoc = doc.substring(startIndex + startStr1.length, doc.length)
        val endIndex = newDoc.indexOf(endString)
        newDoc = newDoc.substring(0, endIndex)
        if (newDoc.isEmpty()) {
            return null
        }

        val html = AcfunApiService.appJson.decodeFromString<HtmlContent>(newDoc).html

        val jxDocument = JXDocument.create(html)
        val list = ArrayList<HomeRecommendItem>()
//        val videos = jxDocument.selN("//div[@class='search-video']")
        val videos = jxDocument.selN("//div[@class='video__main']")
        val covers = jxDocument.selN("//div[@class='video__cover']")

        val coverMap = hashMapOf<String, String>()

        if (videos.size < 60) {
            hasMore = false
        }

        for (cover in covers) {
            val element = cover.asElement()
            coverMap[element.getElementsByAttribute("data-click-log").attr("href")] =
                element.getElementsByTag("img").attr("src")
        }
        var count = 0
        for (video in videos) {
            val element = video.asElement()
            val title = element.getElementsByClass("video__main__title")
            val userName = element.getElementsByClass("user-name")
            val intro = element.getElementsByClass("video__main__intro")

            val videoId = element.getElementsByAttribute("data-click-log").attr("href")
            val body = AcContent()
            body.title = title.text()
            body.up = userName.text()
            body.info = intro.text()
            body.img = coverMap[videoId]
            body.url = "https://www.acfun.cn$videoId"
            val videoItem = HomeRecommendItem()
            videoItem.viewType = HomeRecommendItem.VIEW_TYPE_CARD
            videoItem.item = body
            videoItem.innerItemCount = count
            count++
            list.add(videoItem)
        }
        page++
        return list
    }
}
