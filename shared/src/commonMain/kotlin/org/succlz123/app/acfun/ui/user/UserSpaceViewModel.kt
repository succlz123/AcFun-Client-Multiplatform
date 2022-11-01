package org.succlz123.app.acfun.ui.user

import androidx.compose.ui.focus.FocusRequester
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import org.seimicrawler.xpath.JXDocument
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.html.HtmlContent
import org.succlz123.app.acfun.vm.ScreenPageViewModel
import org.succlz123.lib.result.screenResultDataNone
import org.succlz123.lib.screen.result.ScreenResult

class UserSpaceViewModel : ScreenPageViewModel() {

    val userSpaceState = MutableStateFlow<ScreenResult<ArrayList<AcContent>>>(ScreenResult.Uninitialized)

    val contentFocusParent = FocusRequester()

    init {
        page = 1
    }

    override fun onCleared() {
        super.onCleared()
        userSpaceState.value = ScreenResult.Uninitialized
    }

    fun loadMoreUpSpace(id: String, isRefresh: Boolean = false) {
        if (userSpaceState.value is ScreenResult.Loading) {
            return
        }
        if (isRefresh) {
            hasMore = true
            page = 1
        }
        if (!hasMore) {
            return
        }
        if (isRefresh) {
            userSpaceState.value = ScreenResult.Loading()
        } else {
            userSpaceState.value = ScreenResult.Loading(userSpaceState.value.invoke())
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val data = getUpSpace(id)
                var cur = userSpaceState.value.invoke()
                if (data.isNullOrEmpty()) {
                    if (cur.isNullOrEmpty()) {
                        userSpaceState.value = screenResultDataNone()
                    } else {
                        hasMore = false
                    }
                } else {
                    if (cur == null) {
                        cur = ArrayList()
                    }
                    cur.addAll(data)
                    page++
                    userSpaceState.value = ScreenResult.Success(cur)
                }
            } catch (e: Exception) {
                userSpaceState.value = ScreenResult.Fail(e)
            }
        }
    }

    private suspend fun getUpSpace(id: String): List<AcContent>? {
        var originStr =
            AcfunApiService.httpClient.get("https://www.acfun.cn/u/${id}?quickViewId=ac-space-video-list&reqID=1&ajaxpipe=1&type=video&order=newest") {
                parameter("pageSize", pageSize)
                parameter("page", page)
                parameter("t", System.currentTimeMillis())
            }.body<String?>()
        if (originStr.isNullOrEmpty()) {
            return null
        }
        originStr = originStr.removeSuffix("/*<!-- fetch-stream -->*/")
        if (originStr.isEmpty()) {
            return null
        }
        val date = AcfunApiService.appJson.decodeFromString<HtmlContent?>(originStr)?.html
        if (date.isNullOrEmpty()) {
            return null
        } else {
            val jxDocument = JXDocument.create(date)
            val list = jxDocument.selN("//div[@id='ac-space-video-list']").firstOrNull()?.asElement()
                ?.getElementsByClass("ac-space-video")
            if (list == null) {
                return null
            } else {
                return list.map {
                    val acContent = AcContent()
                    acContent.title = it.getElementsByClass("title").text()
                    acContent.up = it.getElementsByClass("play-info").text()
                    acContent.img = it.getElementsByTag("img").attr("src")
                    acContent.url = "https://www.acfun.cn${it.attr("href")}"
                    acContent
                }.apply {
                    page++
                    if (size <= 10) {
                        hasMore = false
                    }
                }
            }
        }
    }
}
