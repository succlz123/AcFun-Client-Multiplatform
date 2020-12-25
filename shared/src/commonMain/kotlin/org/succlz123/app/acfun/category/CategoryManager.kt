package org.succlz123.app.acfun.category

object CategoryManager {
    private val AC_ROOT = 1

    val NAME_LIVE = "直播"
    val NAME_ABOUT = "关于"

    val NAME_RECOMMEND = "推荐"

    val NAME_JUSTICE = "AC正义"
    val NAME_BANGUMI = "番剧"
    val NAME_ANIME = "动画"
//    val NAME_ANIME_97 = "动画综合"
//    val NAME_ANIME_395 = "短片动画"
//    val NAME_ANIME_101 = "MAD·AMV"
//    val NAME_ANIME_102 = "MMD·3D"
//    val NAME_ANIME_444 = "虚拟偶像"
//    val NAME_ANIME_98 = "动画资讯"
//    val NAME_ANIME_103 = "COSPLAY·声优"
//    val NAME_ANIME_336 = "布袋·特摄"

    val NAME_ANIME_106 = "动画综合"
    val NAME_ANIME_190 = "短片动画"
    val NAME_ANIME_107 = "MAD·AMV"
    val NAME_ANIME_108 = "MMD·3D"
    val NAME_ANIME_207 = "虚拟偶像"
    val NAME_ANIME_159 = "动画资讯"
    val NAME_ANIME_133 = "COSPLAY·声优"
    val NAME_ANIME_99 = "布袋·特摄"

    val NAME_FUN = "娱乐"
    val NAME_FUN_206 = "搞笑"
    val NAME_FUN_87 = "鬼畜调教"
    val NAME_FUN_188 = "娱乐圈"

    val NAME_LIFE = "生活"
    val NAME_LIFE_86 = "生活日常"
    val NAME_LIFE_88 = "萌宠"
    val NAME_LIFE_89 = "美食"
    val NAME_LIFE_204 = "旅行"
    val NAME_LIFE_127 = "手工·绘画"
    val NAME_LIFE_205 = "美妆·造型"

    val NAME_MUSIC = "音乐"
    val NAME_MUSIC_136 = "原创·翻唱"
    val NAME_MUSIC_137 = "演奏·乐器"
    val NAME_MUSIC_103 = "Vocaloid"
    val NAME_MUSIC_139 = "综合音乐·现场"
    val NAME_MUSIC_185 = "音乐选集"

    val NAME_DANCE = "舞蹈"
    val NAME_DANCE_134 = "宅舞"
    val NAME_DANCE_135 = "综合舞蹈"
    val NAME_DANCE_129 = "偶像"
    val NAME_DANCE_208 = "中国舞"

    val NAME_GAME = "游戏"
    val NAME_GAME_84 = "主机单机"
    val NAME_GAME_186 = "网络游戏"
    val NAME_GAME_145 = "电子竞技"
    val NAME_GAME_85 = "英雄联盟"
    val NAME_GAME_187 = "手机游戏"
    val NAME_GAME_165 = "桌游卡牌"
    val NAME_GAME_72 = "Mugen"
    val NAME_GAME_210 = "我的世界"
    val NAME_GAME_214 = "王者荣耀"

    val NAME_TECH = "科技"
    val NAME_TECH_90 = "科技制造"
    val NAME_TECH_189 = "人文科普"
    val NAME_TECH_122 = "汽车"
    val NAME_TECH_91 = "数码"
    val NAME_TECH_151 = "演讲·公开课"
    val NAME_TECH_149 = "广告"
    val NAME_TECH_209 = "手办模玩"

    val NAME_MOVIE = "影视"
    val NAME_MOVIE_192 = "预告·花絮"
    val NAME_MOVIE_193 = "电影杂谈"
    val NAME_MOVIE_194 = "剧透社"
    val NAME_MOVIE_195 = "综艺Show"
    val NAME_MOVIE_196 = "纪实·短片"

    val NAME_SPORT = "体育"
    val NAME_SPORT_152 = "综合体育"
    val NAME_SPORT_94 = "足球"
    val NAME_SPORT_95 = "篮球"
    val NAME_SPORT_153 = "搏击健身"
    val NAME_SPORT_93 = "极限竞速"

    val NAME_FLASHPOND = "鱼塘"
    val NAME_FLASHPOND_183 = "普法安全"
    val NAME_FLASHPOND_92 = "国防军事"
    val NAME_FLASHPOND_131 = "历史"
    val NAME_FLASHPOND_132 = "新鲜事·正能量"

    val MAP = mapOf(
        6 to NAME_ANIME,
        4 to NAME_FUN,
        274 to NAME_LIFE,
        20 to NAME_MUSIC,
        21 to NAME_DANCE,
        19 to NAME_GAME,
        167 to NAME_TECH,
        11 to NAME_MOVIE,
        168 to NAME_SPORT,
        166 to NAME_FLASHPOND
    )

    val PRIORITY_MAP = mapOf(
        1 to 1,
        60 to 2,
        201 to 3,
        58 to 4,
        123 to 5,
        59 to 6,
        70 to 7,
        68 to 8,
        69 to 9,
        125 to 10
    )

    val SUB_MAP = mapOf(
        106 to NAME_ANIME_106,
        107 to NAME_ANIME_107,
        108 to NAME_ANIME_108,
        133 to NAME_ANIME_133,
        159 to NAME_ANIME_159,
        190 to NAME_ANIME_190,
        207 to NAME_ANIME_207,
        99 to NAME_ANIME_99,

        206 to NAME_FUN_206,
        87 to NAME_FUN_87,
        188 to NAME_FUN_188,

        86 to NAME_LIFE_86,
        88 to NAME_LIFE_88,
        89 to NAME_LIFE_89,
        204 to NAME_LIFE_204,
        127 to NAME_LIFE_127,
        205 to NAME_LIFE_205,

        136 to NAME_MUSIC_136,
        137 to NAME_MUSIC_137,
        103 to NAME_MUSIC_103,
        139 to NAME_MUSIC_139,
        185 to NAME_MUSIC_185,

        134 to NAME_DANCE_134,
        135 to NAME_DANCE_135,
        129 to NAME_DANCE_129,
        208 to NAME_DANCE_208,

        84 to NAME_GAME_84,
        85 to NAME_GAME_85,
        145 to NAME_GAME_145,
        165 to NAME_GAME_165,
        186 to NAME_GAME_186,
        187 to NAME_GAME_187,
        72 to NAME_GAME_72,
        210 to NAME_GAME_210,
        214 to NAME_GAME_214,

        90 to NAME_TECH_90,
        122 to NAME_TECH_122,
        149 to NAME_TECH_149,
        151 to NAME_TECH_151,
        189 to NAME_TECH_189,
        91 to NAME_TECH_91,
        209 to NAME_TECH_209,

        192 to NAME_MOVIE_192,
        193 to NAME_MOVIE_193,
        194 to NAME_MOVIE_194,
        195 to NAME_MOVIE_195,
        196 to NAME_MOVIE_196,

        152 to NAME_SPORT_152,
        153 to NAME_SPORT_153,
        93 to NAME_SPORT_93,
        94 to NAME_SPORT_94,
        95 to NAME_SPORT_95,

        183 to NAME_FLASHPOND_183,
        131 to NAME_FLASHPOND_131,
        132 to NAME_FLASHPOND_132,
        92 to NAME_FLASHPOND_92
    )

    private var sRoot: Meta? = null

    fun getRootCategory(): Meta {
        var root = sRoot
        if (root != null) {
            return root
        }
        root = Meta()
//        root.children.add(Meta(-1, NAME_JUSTICE, -1))
        root.children.add(Meta(1, NAME_ANIME, -1))
        root.children.add(Meta(60, NAME_FUN, -1))
        root.children.add(Meta(201, NAME_LIFE, -1))
        root.children.add(Meta(58, NAME_MUSIC, -1))
        root.children.add(Meta(123, NAME_DANCE, -1))
        root.children.add(Meta(59, NAME_GAME, -1))
        root.children.add(Meta(70, NAME_TECH, -1))
        root.children.add(Meta(68, NAME_MOVIE, -1))
        root.children.add(Meta(69, NAME_SPORT, -1))
        root.children.add(Meta(125, NAME_FLASHPOND, -1))
        sRoot = root
        return root
    }

    fun getHomeCategory(): Meta {
        val root = Meta()
        val default = Meta()
        default.id = -1
        default.name = NAME_RECOMMEND
        root.children.add(default)
        root.children.addAll(getRootCategory().children)
        return root
    }

    class Meta(
        var id: Int = 0,
        var parentId: Int = 0,
        var name: String? = null,
        var children: ArrayList<Meta> = ArrayList()
    ) : Cloneable {

        constructor(id: Int, name: String, parentId: Int) : this() {
            this.id = id
            this.name = name
            this.parentId = parentId
        }

        constructor(o: Meta) : this() {
            id = o.id
            parentId = o.parentId
            name = o.name
            if (o.hasChild()) {
                children = ArrayList(o.children)
            }
        }

        fun hasChild(): Boolean {
            return !children.isEmpty()
        }

        fun remove(vararg ids: Int) {
            if (ids.isNotEmpty() && hasChild()) {
                for (id in ids) {
                    children.remove(getChild(id))
                }
            }
        }

        fun getChild(id: Int): Meta? {
            if (this.id == id) {
                return this
            }
            if (hasChild()) {
                for (meta in children) {
                    if (meta.id == id) {
                        return meta
                    }
                }
            }
            return null
        }

        fun size(): Int {
            return children.size
        }

        public override fun clone(): Meta {
            return Meta(this)
        }

        override fun toString(): String {
            return "Meta {$id:$name}"
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) {
                return true
            }
            if (other == null || javaClass != other.javaClass) {
                return false
            }
            val that = other as Meta?
            return id == that?.id
        }

        override fun hashCode(): Int {
            var result = id
            result = 31 * result + parentId
            result = 31 * result + children.hashCode()
            return result
        }
    }
}
