package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable

@Serializable
data class RankList(
    val rankList: ArrayList<Rank>?,
    val requestId: String?,
    val result: Int?
)

@Serializable
data class Rank(
    val bananaCount: Int?,
    val bananaCountShow: String?,
    val belongToSpecifyArubamu: Boolean?,
    val channel: Channel?,
    val channelId: Int?,
    val channelName: String?,
    val commentCount: Int?,
    val commentCountRealValue: Int?,
    val commentCountShow: String?,
    val commentCountTenThousandShow: String?,
    val contentId: Int?,
    val contentTitle: String?,
    val contentType: Int?,
    val contributeTime: Long?,
    val contributionCount: Int?,
    val coverCdnUrls: List<CoverCdnUrl>?,
    val coverImgInfo: CoverImgInfo?,
    val coverUrl: String?,
    val createTime: String?,
    val createTimeMillis: Long?,
    val danmakuCount: Int?,
    val danmakuCountShow: String?,
    val danmuCount: Int?,
    val description: String?,
    val disableEdit: Boolean?,
    val dougaId: String?,
    val duration: Int?,
    val durationMillis: Int?,
    val fansCount: Int?,
    val giftPeachCount: Int?,
    val giftPeachCountShow: String?,
    val groupId: String?,
    val hasHotComment: Boolean?,
    val isDislike: Boolean?,
    val isFavorite: Boolean?,
    val isFollowing: Boolean?,
    val isLike: Boolean?,
    val isRewardSupportted: Boolean?,
    val isThrowBanana: Boolean?,
    val likeCount: Int?,
    val likeCountShow: String?,
    val picShareUrl: String?,
    val shareCount: Int?,
    val shareCountShow: String?,
    val shareUrl: String?,
    val status: Int?,
    val stowCount: Int?,
    val stowCountShow: String?,
    val superUbb: Boolean?,
    val title: String?,
    val user: User?,
    val userId: Int?,
    val userImg: String?,
    val userName: String?,
    val userSignature: String?,
    val videoCover: String?,
    val videoList: List<Video>?,
    val viewCount: Int?,
    val viewCountShow: String?
)

@Serializable
data class CoverImgInfo(
    val animated: Boolean?,
    val height: Int?,
    val size: Int?,
    val thumbnailImage: ThumbnailImage?,
    val thumbnailImageCdnUrl: String?,
    val type: Int?,
    val width: Int?
)

@Serializable
data class ThumbnailImage(
    val cdnUrls: List<CdnUrl>?
)

@Serializable
data class CdnUrl(
    val freeTrafficCdn: Boolean?,
    val url: String?
)

@Serializable
data class UserHeadImgInfo(
    val animated: Boolean?,
    val height: Int?,
    val size: Int?,
    val thumbnailImage: ThumbnailImageX?,
    val thumbnailImageCdnUrl: String?,
    val type: Int?,
    val width: Int?
)

@Serializable
data class ThumbnailImageX(
    val cdnUrls: List<CdnUrlX>?
)

@Serializable
data class CdnUrlX(
    val freeTrafficCdn: Boolean?,
    val url: String?
)