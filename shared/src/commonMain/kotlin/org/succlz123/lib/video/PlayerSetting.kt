package org.succlz123.lib.video

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import org.succlz123.app.acfun.ui.main.vm.HomeSettingViewModel
import org.succlz123.lib.setting.createSettings

class PlayerSetting(
    val setting: Settings = createSettings("AcFun_APP"),
    val speed: MutableStateFlow<String> = MutableStateFlow(
        setting.getString(
            HomeSettingViewModel.KEY_VIDEO_SPEED,
            "1.0"
        )
    ),
    val ratio: MutableStateFlow<String> = MutableStateFlow("适应"),
    val danmaku: MutableStateFlow<Boolean> = MutableStateFlow(
        setting.getBoolean(
            HomeSettingViewModel.KEY_DANMAKU_ENABLE,
            false
        )
    ),
) {
    companion object {
        val speedTags = arrayListOf("0.5", "0.75", "1.0", "1.5", "2.0")
        val ratioTags = arrayListOf("4:3", "16:9", "填充", "原始", "适应")
        val danmakuTags = arrayListOf("开启", "关闭")
    }
}
