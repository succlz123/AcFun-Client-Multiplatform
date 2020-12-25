package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable

@Serializable
data class LiveRoomList(
    val liveList: List<LiveX>? = null,
    val totalCount: Int? = null
)

@Serializable
data class LiveX(
    val action: Int? = null,
    val authorId: Int? = null,
    val bizCustomData: String? = null,
    val cdnAuthBiz: Int? = null,
    val coverUrls: List<String>? = null,
    val createTime: Long? = null,
    val disableDanmakuShow: Boolean? = null,
    val formatLikeCount: String? = null,
    val formatOnlineCount: String? = null,
    val groupId: String? = null,
    val hasFansClub: Boolean? = null,
    val href: String? = null,
    val likeCount: Int? = null,
    val liveId: String? = null,
    val onlineCount: Int? = null,
    val paidShowUserBuyStatus: Boolean? = null,
    val panoramic: Boolean? = null,
    val portrait: Boolean? = null,
    val requestId: String? = null,
    val streamName: String? = null,
    val title: String? = null,
    val type: TypeX? = null,
    val user: UserX? = null
)

@Serializable
data class TypeX(
    val categoryId: Int? = null, val categoryName: String? = null, val id: Int? = null, val name: String? = null
)

@Serializable
data class UserX(
    val action: Int? = null,
    val avatarFrame: Int? = null,
    val avatarFrameMobileImg: String? = null,
    val avatarFramePcImg: String? = null,
    val avatarImage: String? = null,
    val contributeCount: String? = null,
    val contributeCountValue: Int? = null,
    val fanCount: String? = null,
    val fanCountValue: Int? = null,
    val followingCount: String? = null,
    val followingCountValue: Int? = null,
    val followingStatus: Int? = null,
    val gender: Int? = null,
    val headCdnUrls: List<HeadCdnUrlX>? = null,
    val headUrl: String? = null,
    val href: String? = null,
    val id: String? = null,
    val isFollowed: Boolean? = null,
    val isFollowing: Boolean? = null,
    val isJoinUpCollege: Boolean? = null,
    val liveId: String? = null,
    val name: String? = null,
    val nameColor: Int? = null,
    val sexTrend: Int? = null,
    val signature: String? = null,
    val userHeadImgInfo: UserHeadImgInfoX? = null,
    val verifiedText: String? = null,
    val verifiedType: Int? = null,
    val verifiedTypes: List<Int>? = null
)

@Serializable
data class HeadCdnUrlX(
    val freeTrafficCdn: Boolean? = null, val url: String? = null
)

@Serializable
data class UserHeadImgInfoX(
    val animated: Boolean? = null,
    val height: Int? = null,
    val size: Int? = null,
    val thumbnailImage: ThumbnailImageX? = null,
    val thumbnailImageCdnUrl: String? = null,
    val type: Int? = null,
    val width: Int? = null
)