package org.succlz123.app.acfun.danmaku

import kotlinx.serialization.Serializable

@Serializable
class VideoDanmaku(
    val addCount: Int = 0,
    var danmakus: ArrayList<DanmakuBean>? = null,
    val fetchTime: Long? = null,
    val onlineCount: Int = 0,
    val positionFromInclude: Int = 0,
    val positionToExclude: Int = 0,
    val result: Int = 0,
    val styleDanmakuCount: Int = 0,
    val totalCount: Int = 0,
    val totalCountShow: String? = null
)