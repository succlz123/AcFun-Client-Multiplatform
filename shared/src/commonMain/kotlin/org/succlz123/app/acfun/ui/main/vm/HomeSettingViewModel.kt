package org.succlz123.app.acfun.ui.main.vm

import androidx.compose.runtime.mutableStateOf
import org.succlz123.lib.setting.createSettings
import org.succlz123.lib.vm.BaseViewModel

class HomeSettingViewModel : BaseViewModel() {

    companion object {

        val KEY_DANMAKU_ENABLE = "KEY_DANMAKU_ENABLE"
        val danmakuList = arrayListOf("开启弹幕", "关闭弹幕")

        val KEY_VIDEO_SPEED = "KEY_VIDEO_SPEED"
        val speedList = arrayListOf("0.5", "0.75", "1.0", "1.5", "2.0")
    }

    val setting = createSettings("AcFun_APP")

    val danmakuEnable = mutableStateOf(setting.getBoolean(KEY_DANMAKU_ENABLE, false))

    val videoSpeed = mutableStateOf(setting.getString(KEY_VIDEO_SPEED, "1.0"))
}
