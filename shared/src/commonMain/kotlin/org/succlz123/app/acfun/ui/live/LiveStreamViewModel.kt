package org.succlz123.app.acfun.ui.live

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.succlz123.app.acfun.api.AcfunApiService
import org.succlz123.app.acfun.api.bean.LiveStream
import org.succlz123.app.acfun.api.bean.LoginForLive
import org.succlz123.app.acfun.api.bean.Representation
import org.succlz123.app.acfun.vm.ScreenPageViewModel
import org.succlz123.lib.result.screenResultDataNone
import org.succlz123.lib.screen.result.ScreenResult

class LiveStreamViewModel : ScreenPageViewModel() {

    val liveSteam = MutableStateFlow<ScreenResult<List<Representation>>>(ScreenResult.Uninitialized)

    fun getLiveStreamData(roomId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body =
                    AcfunApiService.httpClient.post("https://id.app.acfun.cn/rest/app/visitor/login") {
                        header("cookie", "_did=H5_")
                        header("referer", "https://m.acfun.cn/")
                        setBody(FormDataContent(Parameters.build {
                            append("sid", "acfun.api.visitor")
                            append("param2", "ipsum")
                        }))
                    }.body<LoginForLive?>()
                val userId = body?.userId
                val st = body?.visitor_st
                if (body == null || userId == null || st == null) {
                    liveSteam.value = screenResultDataNone()
                } else {
                    val params = LinkedHashMap<String, String>()
                    params.put("subBiz", "mainApp")
                    params.put("kpn", "ACFUN_APP")
                    params.put("kpf", "PC_WEB")
                    params.put("userId", userId.toString())
                    params.put("did", "H5_")
                    params.put("acfun.api.visitor_st", st)
                    val liveStream =
                        AcfunApiService.httpClient.submitForm(
                            "https://api.kuaishouzt.com/rest/zt/live/web/startPlay",
                            formParameters = Parameters.build {
                                append("authorId", roomId)
                                append("pullStreamType", "FLV")
                                for (param in params) {
                                    append(param.key, param.value)
                                }
                            }
                        ) {
                            header("cookie", "_did=H5_")
                            header("referer", "https://m.acfun.cn/")
                        }.body<LiveStream?>()
                    if (liveStream?.result != 1) {
                        liveSteam.value = screenResultDataNone()
                    } else {
                        val jsonStr = liveStream.live?.videoPlayRes.orEmpty()

                        val jsonObject = AcfunApiService.appJson.decodeFromString<JsonObject>(jsonStr)
                        val liveAdaptiveManifest = jsonObject.get("liveAdaptiveManifest") as? JsonArray
                        val adaptationSet =
                            (liveAdaptiveManifest?.firstOrNull() as? JsonObject)?.get("adaptationSet") as? JsonObject
                        val representations = adaptationSet?.get("representation") as? JsonArray

                        val list = representations?.mapNotNull {
                            val urlInfo = it as? JsonObject
                            if (urlInfo != null) {
                                val representation = Representation()
                                val url = urlInfo.get("url")?.jsonPrimitive?.content.orEmpty()
                                val name = urlInfo.get("name")?.jsonPrimitive?.content.orEmpty()
                                representation.url = url
                                representation.qualityLabel = name
                                representation
                            } else {
                                null
                            }
                        }
                        if (list.isNullOrEmpty()) {
                            liveSteam.value = screenResultDataNone()
                        } else {
                            liveSteam.value = ScreenResult.Success(list)
                        }
                    }
                }
            } catch (e: Exception) {
                liveSteam.value = ScreenResult.Fail(e)
            }
        }
    }
}
