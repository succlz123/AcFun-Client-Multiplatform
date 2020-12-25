package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable
import org.succlz123.app.acfun.danmaku.VideoDanmaku

@Serializable
data class VideoContent(
    val bananaCount: Int = 0,
    val bananaCountShow: String? = null,
    val channel: Channel? = null,
    val commentCount: Int = 0,
    val commentCountRealValue: Int = 0,
    val commentCountShow: String? = null,
    val commentCountTenThousandShow: String? = null,
    val coverCdnUrls: List<CoverCdnUrl>? = null,
    val coverUrl: String? = null,
    val createTime: String? = null,
    val createTimeMillis: Long = 0,
    val currentVideoId: Int = 0,
    val currentVideoInfo: CurrentVideoInfo? = null,
    val danmakuCount: Int = 0,
    val danmakuCountShow: String? = null,
    val description: String? = null,
    val dougaId: String? = null,
    val durationMillis: Int = 0,
    val giftPeachCount: Int = 0,
    val giftPeachCountShow: String? = null,
    val hasHotComment: Boolean = false,
    val isFavorite: Boolean = false,
    val isLike: Boolean = false,
    val isRewardSupportted: Boolean = false,
    val isThrowBanana: Boolean = false,
    val likeCount: Int = 0,
    val likeCountShow: String? = null,
    val mkey: String? = null,
    val originalDeclare: Int = 0,
    val pctr: Double = 0.0,
    val picShareUrl: String? = null,
    val priority: Int = 0,
    val recoReason: RecoReason? = null,
    val result: Int = 0,
    val shareCount: Int = 0,
    val shareCountShow: String? = null,
    val shareUrl: String? = null,
    val status: Int = 0,
    val stowCount: Int = 0,
    val stowCountShow: String? = null,
    val superUbb: Boolean = false,
    val tagList: List<Tag>? = null,
    val title: String? = null,
    val user: User? = null,
    val videoList: ArrayList<Video>? = null,
    val viewCount: Int = 0,
    val viewCountShow: String? = null,
    var playerList: PlayList? = null,
) {
    var danmakuList: VideoDanmaku? = null

    // from 1
    var epIndex: Int = 1
}

@Serializable
data class Channel(
    val id: Int = 0,
    val name: String? = null,
    val parentId: Int = 0,
    val parentName: String? = null
)

@Serializable
data class CoverCdnUrl(
    val freeTrafficCdn: Boolean = false,
    val url: String? = null
)

@Serializable
data class CurrentVideoInfo(
    val danmakuCount: Int = 0,
    val danmakuCountShow: String? = null,
    val durationMillis: Int = 0,
    val fileName: String? = null,
    val id: String? = null,
    val ksPlayJson: String? = null,
    val priority: Int = 0,
    val sizeType: Int = 0,
    val sourceStatus: Int = 0,
    val title: String? = null,
    val uploadTime: Long = 0,
    val visibleType: Int = 0
)

@Serializable
data class RecoReason(
    val desc: String? = null,
    val href: String? = null,
    val tag: String? = null,
    val type: Int = 0
)

@Serializable
data class Tag(
    val id: String? = null,
    val name: String? = null
)

@Serializable
data class User(
    val avatarFrame: Int = 0,
    val comeFrom: String? = null,
    val contributeCount: String? = null,
    val contributeCountValue: Int = 0,
    val fanCount: String? = null,
    val fanCountValue: Int = 0,
    val followingCount: String? = null,
    val followingCountValue: Int = 0,
    val gender: Int = 0,
    val headCdnUrls: List<HeadCdnUrl>? = null,
    val headUrl: String? = null,
    val id: String? = null,
    val isFollowing: Boolean = false,
    val isJoinUpCollege: Boolean = false,
    val name: String? = null,
    val nameColor: Int = 0,
    val nameStyle: String? = null,
    val sexTrend: Int = 0,
    val signature: String? = null,
    val verifiedText: String? = null,
    val verifiedType: Int = 0
)

@Serializable
data class Video(
    val danmakuCount: Int = 0,
    val danmakuCountShow: String? = null,
    val durationMillis: Int = 0,
    val fileName: String? = null,
    val id: String? = null,
    val priority: Int = 0,
    val sizeType: Int = 0,
    val sourceStatus: Int = 0,
    val title: String? = null,
    val uploadTime: Long = 0,
    val visibleType: Int = 0
)

@Serializable
data class HeadCdnUrl(
    val freeTrafficCdn: Boolean = false,
    val url: String? = null
)
