package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable

@Serializable
data class UserSpace(
    var feed: ArrayList<AcContent>? = null,
    var pcursor: String? = null,
    var requestId: String? = null,
    var result: Int = 0,
    var totalNum: Int = 0
) {

    @Serializable
    data class UserSpaceFeed(
        var bananaCount: Int = 0,
        var bananaCountShow: String? = null,
        var channel: UserSpaceChannel? = null,
        var commentCount: Int = 0,
        var commentCountRealValue: Int = 0,
        var commentCountShow: String? = null,
        var commentCountTenThousandShow: String? = null,
        var coverCdnUrls: List<UserSpaceCoverCdnUrl>? = null,
        var coverUrl: String? = null,
        var createTime: String? = null,
        var createTimeMillis: Long = 0,
        var danmakuCount: Int = 0,
        var danmakuCountShow: String? = null,
        var description: String? = null,
        var dougaId: String? = null,
        var durationMillis: Int = 0,
        var giftPeachCount: Int = 0,
        var giftPeachCountShow: String? = null,
        var groupId: String? = null,
        var hasHotComment: Boolean = false,
        var isFavorite: Boolean = false,
        var isLike: Boolean = false,
        var isRewardSupportted: Boolean = false,
        var isThrowBanana: Boolean = false,
        var likeCount: Int = 0,
        var likeCountShow: String? = null,
        var picShareUrl: String? = null,
        var shardCount: String? = null,
        var shareCount: Int = 0,
        var shareCountShow: String? = null,
        var shareUrl: String? = null,
        var status: Int = 0,
        var stowCount: Int = 0,
        var stowCountShow: String? = null,
        var superUbb: Boolean? = null,
        var tagList: List<UserSpaceTag>? = null,
        var user: UserSpaceUser? = null,
        var videoList: List<UserSpaceVideo>? = null,
        var viewCount: Int = 0,
        var viewCountShow: String? = null,
        var title: String? = null
    )
}

@Serializable
data class UserSpaceChannel(
        var id: Int = 0,
        var name: String? = null,
        var parentId: Int = 0,
        var parentName: String? = null
)

@Serializable
data class UserSpaceCoverCdnUrl(
        var freeTrafficCdn: Boolean = false,
        var url: String? = null
)

@Serializable
data class UserSpaceTag(
        var id: String? = null,
        var name: String? = null
)

@Serializable
data class UserSpaceUser(
    var avatarFrame: Int = 0,
    var avatarFrameMobileImg: String? = null,
    var avatarFramePcImg: String? = null,
    var contributeCount: String? = null,
    var contributeCountValue: Int = 0,
    var fanCount: String? = null,
    var fanCountValue: Int = 0,
    var followingCount: String? = null,
    var followingCountValue: Int = 0,
    var gender: Int = 0,
    var headCdnUrls: List<HeadCdnUrl>? = null,
    var headUrl: String? = null,
    var id: String? = null,
    var isFollowing: Boolean? = null,
    var isJoinUpCollege: Boolean? = null,
    var liveId: String? = null,
    var nameColor: Int = 0,
    var sexTrend: Int = 0,
    var verifiedText: String? = null,
    var verifiedType: Int = 0,
    var name: String? = null
)

@Serializable
data class UserSpaceVideo(
        var danmakuCount: Int = 0,
        var danmakuCountShow: String? = null,
        var durationMillis: Int = 0,
        var fileName: String? = null,
        var id: String? = null,
        var priority: Int = 0,
        var sizeType: Int = 0,
        var sourceStatus: Int = 0,
        var title: String? = null,
        var uploadTime: Long = 0,
        var visibleType: Int = 0
)

@Serializable
data class UserSpaceHeadCdnUrl(
        var freeTrafficCdn: Boolean = false,
        var url: String? = null
)
