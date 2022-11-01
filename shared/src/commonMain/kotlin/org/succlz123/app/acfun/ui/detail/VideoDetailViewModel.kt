package org.succlz123.app.acfun.ui.detail

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import org.jsoup.nodes.Element
import org.seimicrawler.xpath.JXDocument
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.PlayList
import org.succlz123.app.acfun.api.bean.VideoContent
import org.succlz123.app.acfun.danmaku.DanmakuBean
import org.succlz123.lib.focus.FocusNode
import org.succlz123.lib.network.HttpX
import org.succlz123.lib.result.screenResultDataNone
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.viewmodel.ScreenViewModel

class VideoDetailViewModel : ScreenViewModel() {

    val videoContentState = MutableStateFlow<ScreenResult<VideoContent>>(ScreenResult.Uninitialized)

    var playVideoContentLoadingState: Boolean = false

    val playVideoContentEvent = MutableSharedFlow<ScreenResult<VideoContent>>()

    var downloadVideoContentLoadingState: Boolean = false

    val downloadVideoContentEvent = MutableSharedFlow<ScreenResult<VideoContent>>()

    private var currentPos = 1

    val userSpaceFocusParent = FocusRequester()

    val episodeFocusParent = FocusRequester()

    var currentFocusNode = MutableStateFlow(FocusNode(tag = "Episode"))

    override fun onCleared() {
        super.onCleared()
        currentPos = 1
        videoContentState.value = ScreenResult.Uninitialized
    }

    fun getDetail(acContent: AcContent, force: Boolean = false) {
        if (videoContentState.value is ScreenResult.Success && !force) {
            return
        }
        if (videoContentState.value is ScreenResult.Loading) {
            return
        }
        videoContentState.value = ScreenResult.Loading()
        currentPos = 1
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getFromNetwork(acContent.url.orEmpty())
                if (response == null) {
                    videoContentState.value = screenResultDataNone()
                } else {
                    videoContentState.value = ScreenResult.Success(response)
                }
            } catch (e: Exception) {
                videoContentState.value = ScreenResult.Fail(e)
            }
        }
    }

    // https://www.acfun.cn/v/ac13367963_2
    fun play(acContent: AcContent, position: Int) {
        if (position == 1) {
            val page1Data = videoContentState.value.invoke()
            if (page1Data != null) {
                viewModelScope.launch { playVideoContentEvent.emit(ScreenResult.Success(page1Data)) }
            }
            return
        }
        if (playVideoContentLoadingState) {
            return
        }
        playVideoContentLoadingState = true
        currentPos = position
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getFromNetwork(acContent.url + "_" + position)
                if (response == null) {
                    playVideoContentEvent.emit(screenResultDataNone())
                } else {
                    playVideoContentEvent.emit(ScreenResult.Success(response.apply { epIndex = position }))
                }
                playVideoContentLoadingState = false
            } catch (e: Exception) {
                viewModelScope.launch { playVideoContentEvent.emit(ScreenResult.Fail(e)) }
                playVideoContentLoadingState = false
            }
        }
    }

    fun download(acContent: AcContent, position: Int) {
        if (position == 1) {
            val page1Data = videoContentState.value.invoke()
            if (page1Data != null) {
                viewModelScope.launch { downloadVideoContentEvent.emit(ScreenResult.Success(page1Data)) }
            }
            return
        }
        if (downloadVideoContentLoadingState) {
            return
        }
        downloadVideoContentLoadingState = true
        currentPos = position
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = getFromNetwork(acContent.url + "_" + position)
                if (response == null) {
                    downloadVideoContentEvent.emit(screenResultDataNone())
                } else {
                    downloadVideoContentEvent.emit(ScreenResult.Success(response.apply { epIndex = position }))
                }
                downloadVideoContentLoadingState = false
            } catch (e: Exception) {
                viewModelScope.launch { downloadVideoContentEvent.emit(ScreenResult.Fail(e)) }
                downloadVideoContentLoadingState = false
            }
        }
    }

    private suspend fun getFromNetwork(url: String): VideoContent? {
        val doc = HttpX.doGet(url)
//        val mdoc = HttpX.doGet("https://m.acfun.cn/v/?ac=" + url.split("/ac").lastOrNull())
        if (doc.isNullOrEmpty()) {
            return null
        }
        val jxDocument = JXDocument.create(doc)
//        val mJxDocument = JXDocument.create(mdoc)
//        val recommend = mJxDocument.selN("//div[@class='reco-list-wrapper']")
        val sel = jxDocument.sel("//script")
        for (any in sel) {
            if (any is Element) {
                val data = any.data()
                val startFlag = "window.pageInfo = window.videoInfo ="
                val endFlag = "};"
                if (data.contains(startFlag)) {
                    val startPos = data.indexOf(startFlag, 0) + startFlag.length
                    val endPos = data.indexOf(endFlag, 0)
                    val content = data.slice(IntRange(startPos, endPos))
                    val json = AcfunApiService.appJson.decodeFromString<VideoContent?>(content)
//                    json?.videoList?.addAll(json.videoList)
//                    json?.videoList?.addAll(json.videoList)
//                    json?.videoList?.addAll(json.videoList)
//                    json?.videoList?.addAll(json.videoList)
//                    json?.videoList?.addAll(json.videoList)
//                    json?.videoList?.addAll(json.videoList)
//                    json?.videoList?.addAll(json.videoList)
                    val playList =
                        AcfunApiService.appJson.decodeFromString<PlayList?>(json?.currentVideoInfo?.ksPlayJson.orEmpty())
                    json?.playerList = playList
                    try {
                        val result = AcfunApiService.getDanmaku(json?.currentVideoId?.toString().orEmpty())
                        json?.danmakuList = result
                    } catch (_: Exception) {
                    }
                    val danmakus = json?.danmakuList?.danmakus
                    val resultReduceDanmakus = arrayListOf<DanmakuBean>()
                    if (!danmakus.isNullOrEmpty()) {
                        val groupList = danmakus.groupBy { it.body }
                        groupList.forEach { t, u ->
                            if (u.size == 1) {
                                resultReduceDanmakus.addAll(u)
                            } else {
                                var countList = u
                                while (countList.isNotEmpty()) {
                                    if (countList.size == 1) {
                                        resultReduceDanmakus.addAll(countList)
                                        break
                                    }
                                    val firstDanmaku = countList.first()
                                    val hasNotSameTime =
                                        countList.filter { firstDanmaku.position + 16000L < it.position }
                                    firstDanmaku.sameCount = countList.size - hasNotSameTime.size
                                    countList = hasNotSameTime
                                    resultReduceDanmakus.add(firstDanmaku)
                                }
                            }
                        }
                    }
                    return json
                }
            }
        }
        return null
    }
}
