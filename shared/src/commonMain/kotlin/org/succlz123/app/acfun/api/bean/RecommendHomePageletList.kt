package org.succlz123.app.acfun.api.bean

import kotlinx.serialization.Serializable

@Serializable
data class RecommendHomePageletList(
    val container: String? = null,
    val css: List<String>? = null,
    val html: String? = null,
    val id: String? = null,
    val js: List<String>? = null,
    val mode: String? = null,
    val scripts: List<String>? = null,
)