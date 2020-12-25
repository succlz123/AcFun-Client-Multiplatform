package org.succlz123.app.acfun.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.*
import androidx.compose.runtime.mutableStateOf
import org.succlz123.lib.vm.BaseViewModel

class MainViewModel : BaseViewModel() {

    val leftTitle = arrayListOf("首页", "直播", "排行", "搜索", "下载", "设置")

    val leftIcon =
        arrayListOf(
            Icons.Sharp.Home,
            Icons.Sharp.Face,
            Icons.Sharp.DateRange,
            Icons.Sharp.Search,
            Icons.Sharp.Done,
            Icons.Sharp.Settings
        )

    val leftSelectItem = mutableStateOf(0)
}
