package org.succlz123.app.acfun.danmaku

import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable

@Serializable
class DanmakuBean(
    val advancedDanmakuBody: String? = null,
    val advancedDanmakuExtData: String? = null,
    val body: String? = null,
    val color: Int = 0,
    val createTime: Long? = null,
    val danmakuAvatarUrl: String? = null,
    val danmakuId: Int? = null,
    val danmakuImgUrl: String? = null,
    val danmakuStyle: Int = 0,
    val danmakuType: Int = 0,
    val isLike: Boolean = false,
    val likeCount: Int = 0,
    val mode: Int = 0,
    val position: Long = 0,
    val rank: Int = 0,
    val roleId: Int? = null,
    val size: Int = 0,
    val userId: Long = 0
) {

    var sameCount = 1 // itself

    fun displayStr(): String {
        return if (sameCount > 1) {
            "${body.orEmpty()} x$sameCount"
        } else {
            body.orEmpty()
        }
    }

    var width = 0

    var isShow = mutableStateOf(false)
    var showIndex = -1
    var workState = mutableStateOf(DanmkuAnimationState.OUT_WORK)

    override fun toString(): String {
        return body.orEmpty()
    }
}

enum class DanmkuAnimationState {
    IN_WORK, IN_ANIMATION, OUT_WORK
}
