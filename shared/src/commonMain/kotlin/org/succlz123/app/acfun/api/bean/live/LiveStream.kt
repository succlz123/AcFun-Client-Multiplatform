package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginForLive(
    val acSecurity: String? = null,
    @SerialName("acfun.api.visitor_st") val visitor_st: String? = null,
    val result: Int? = null,
    val userId: Long? = null
)

@Serializable
data class LiveStream(
    @SerialName("data") val live: LiveStreamData? = null,
    val host: String? = null, val result: Int = -1
)

@Serializable
data class LiveStreamData(
    val availableTickets: List<String>?,
    val caption: String?,
    val config: LiveStreamConfig?,
    val enterRoomAttach: String?,
    val liveId: String?,
    val liveStartTime: Long?,
    val notices: List<LiveStreamNotice>?,
    val panoramic: Boolean?,
    val ticketRetryCount: Int?,
    val ticketRetryIntervalMs: Int?,
    val videoPlayRes: String?
)

@Serializable
data class LiveStreamConfig(
    val giftSlotSize: Int
)

@Serializable
data class LiveStreamNotice(
    val notice: String, val userGender: String, val userId: Int, val userName: String
)

@Serializable
data class LiveInnerJson(val liveAdaptiveManifest: LiveAdaptiveManifest?)

@Serializable
data class LiveAdaptiveManifest(
    val adaptationSet: LiveAdaptationSet?,
    val freeTrafficCdn: Boolean,
    val hideAuto: Boolean,
    val type: String,
    val version: String
)

@Serializable
data class LiveAdaptationSet(
    val gopDuration: Int, val representation: List<LiveRepresentation>?
)

@Serializable
data class LiveRepresentation(
    val bitrate: Int,
    val defaultSelect: Boolean,
    val enableAdaptive: Boolean,
    val hidden: Boolean,
    val id: Int,
    val level: Int,
    val mediaType: String,
    val name: String,
    val qualityType: String,
    val url: String?
)