package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable

@Serializable
class AcContent(
    var title: String? = null,
    var up: String? = null,
    var info: String? = null,
    var img: String? = null,
    var time: String? = null,
    var url: String? = null,
    var view: Int? = null,
    var type: Int = TYPE_VIDEO
) {
    companion object {
        const val TYPE_VIDEO = 1
        const val TYPE_LIVE = 2
    }
}
