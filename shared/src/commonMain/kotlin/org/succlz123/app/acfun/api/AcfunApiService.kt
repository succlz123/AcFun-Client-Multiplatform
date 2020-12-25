package org.succlz123.app.acfun.api

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.succlz123.app.acfun.AppBuildConfig
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.api.bean.LiveRoomList
import org.succlz123.app.acfun.danmaku.DanmakuBean
import org.succlz123.app.acfun.danmaku.VideoDanmaku


object AcfunApiService {
    const val BASE_ACFUN_NEW_API_URL = "https://api-new.app.acfun.cn"
    const val BASE_ACFUN_URL = "https://www.acfun.cn"

    val appJson = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    val httpClient = HttpClient {
        defaultRequest {
//            host = "123"
//            header("123", "123")
        }
        install(Logging) {
            level = LogLevel.HEADERS
            logger = object : Logger {
                override fun log(message: String) {
                    if (AppBuildConfig.isDebug) {
                        println("HTTP Client: message: $message")
                    }
                }
            }
        }
        install(ContentNegotiation) {
            json(appJson)
        }
    }

    suspend fun getDanmaku(videoId: String): VideoDanmaku? {
        var isEnd = false
        var page = 0
        val danmakus = arrayListOf<DanmakuBean>()
        var vd: VideoDanmaku? = null
        while (!isEnd) {
            val result = httpClient.post("https://www.acfun.cn/rest/pc-direct/new-danmaku/pollByPosition") {
                setBody(FormDataContent(Parameters.build {
                    append("resourceId", videoId)
                    append("positionFromInclude", (page * 60000).toString())
                    append("positionToExclude", ((page + 1) * 60000).toString())
                    append("enableAdvanced", "false")
                }))
            }.body<VideoDanmaku?>()
            page++
            if (result == null || result.addCount == 0) {
                isEnd = true
            }
            if (vd == null) {
                vd = result
            }
            danmakus.addAll(result?.danmakus.orEmpty())
        }
        vd?.danmakus = danmakus
        return vd
    }

    suspend fun getLiveRoomListFromNetwork(page: Int, pageSize: Int): ArrayList<HomeRecommendItem>? {
        val result = httpClient.get("https://live.acfun.cn/api/channel/list") {
            parameter("count", pageSize)
            parameter("pcursor", page)
        }.body<LiveRoomList>().liveList
        if (result.isNullOrEmpty()) {
            return null
        }
        var count = 0
        val list = ArrayList<HomeRecommendItem>()
        result.forEach {
            val body = AcContent()
            body.title = it.title
            body.up = it.user?.name
            body.img = it.coverUrls?.firstOrNull()
            body.type = AcContent.TYPE_LIVE
            body.time = it.type?.name
            body.view = it.onlineCount
            body.url = it.href
//            body.url = "https://live.acfun.cn/live/" + it.href
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