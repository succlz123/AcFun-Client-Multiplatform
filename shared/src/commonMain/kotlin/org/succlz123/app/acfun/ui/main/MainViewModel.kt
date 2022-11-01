package org.succlz123.app.acfun.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.*
import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.flow.MutableStateFlow
import org.succlz123.lib.vm.BaseViewModel

class MainViewModel : BaseViewModel() {

    companion object {
        val MAIN_TITLE = arrayListOf("首页", "直播", "排行", "搜索", "下载", "设置")

        val MAIN_ICON =
            arrayListOf(
                Icons.Sharp.Home,
                Icons.Sharp.Face,
                Icons.Sharp.DateRange,
                Icons.Sharp.Search,
                Icons.Sharp.Done,
                Icons.Sharp.Settings
            )
    }

    val leftSelectItem = MutableStateFlow(0)

    val leftFocus = FocusRequester()

    val rightFocus = FocusRequester()
}
