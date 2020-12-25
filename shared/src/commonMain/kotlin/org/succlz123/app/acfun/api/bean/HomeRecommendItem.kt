package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable

@Serializable
class HomeRecommendItem {
    var viewType = VIEW_TYPE_CARD
    var titleId: Int = -1
    var titleStr: String? = null

    var item: AcContent? = null
    var innerItemCount: Int = 0

    companion object {

        const val VIEW_TYPE_CARD = 1
        const val VIEW_TYPE_TITLE = 2
    }
}