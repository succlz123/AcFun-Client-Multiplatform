package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable

@Serializable
data class PlayList(
    var adaptationSet: List<AdaptationSet>? = null,
    var businessType: Int? = null,
    var hideAuto: Boolean? = null,
    var manualDefaultSelect: Boolean? = null,
    var mediaType: Int? = null,
    var stereoMode: Int? = null,
    var version: String? = null
)

@Serializable
data class AdaptationSet(
        var duration: Int? = null,
        var id: Int? = null,
        var representation: List<Representation>? = null
)

@Serializable
data class Representation(
        var avgBitrate: Int? = null,
        var backupUrl: List<String>? = null,
        var codecs: String? = null,
        var comment: String? = null,
        var defaultSelect: Boolean? = null,
        var disableAdaptive: Boolean? = null,
        var feature_p2sp: Boolean? = null,
        var frameRate: Double? = null,
        var height: Int? = null,
        var hidden: Boolean? = null,
        var id: Int? = null,
        var m3u8Slice: String? = null,
        var maxBitrate: Int? = null,
        var quality: Double? = null,
        var qualityLabel: String? = null,
        var qualityType: String? = null,
        var url: String? = null,
        var width: Int? = null
)