package org.succlz123.app.acfun.ui.main.tab

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.set
import org.succlz123.app.acfun.base.AcDivider
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.app.acfun.ui.main.tab.item.MainRightTitleLayout
import org.succlz123.app.acfun.ui.main.vm.HomeSettingViewModel
import org.succlz123.app.acfun.ui.main.vm.HomeSettingViewModel.Companion.KEY_DANMAKU_ENABLE
import org.succlz123.app.acfun.ui.main.vm.HomeSettingViewModel.Companion.KEY_VIDEO_SPEED
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.common.getAppPlatformName
import org.succlz123.lib.screen.viewmodel.viewModel

@Composable
fun MainSettingTab(modifier: Modifier = Modifier) {
    val viewModel = viewModel { HomeSettingViewModel() }
    val scrollState = rememberScrollState()
    MainRightTitleLayout(modifier.verticalScroll(scrollState), text = "设置") {
        val r = Runtime.getRuntime()
        val props = remember { System.getProperties() }
        val map = remember { System.getenv() }
        Column(modifier = Modifier.padding(26.dp, 26.dp, 26.dp, 26.dp).fillMaxSize()) {
            Text(
                text = "播放设置", style = MaterialTheme.typography.h3
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "简易弹幕开关（试验功能）:", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Normal)
                Spacer(modifier = Modifier.width(12.dp))
                HomeSettingViewModel.danmakuList.forEachIndexed { index, s ->
                    Text(
                        modifier = Modifier.padding(12.dp, 0.dp).noRippleClickable {
                            if (index == 0) {
                                viewModel.danmakuEnable.value = true
                                viewModel.setting[KEY_DANMAKU_ENABLE] = true
                            } else {
                                viewModel.danmakuEnable.value = false
                                viewModel.setting[KEY_DANMAKU_ENABLE] = false
                            }
                        },
                        text = s,
                        style = MaterialTheme.typography.body1,
                        color = if (index == 0 && viewModel.danmakuEnable.value || index == 1 && !viewModel.danmakuEnable.value
                        ) {
                            ColorResource.acRed
                        } else {
                            ColorResource.subText
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "默认播放速度:", style = MaterialTheme.typography.h5, fontWeight = FontWeight.Normal)
                Spacer(modifier = Modifier.width(12.dp))
                HomeSettingViewModel.speedList.forEachIndexed { index, s ->
                    Text(
                        modifier = Modifier.padding(12.dp, 0.dp).noRippleClickable {
                            if (index == 0) {
                                viewModel.videoSpeed.value = s
                                viewModel.setting[KEY_VIDEO_SPEED] = s
                            } else {
                                viewModel.videoSpeed.value = s
                                viewModel.setting[KEY_VIDEO_SPEED] = s
                            }
                        },
                        text = s,
                        style = MaterialTheme.typography.body1,
                        color = if (s == viewModel.videoSpeed.value) {
                            ColorResource.acRed
                        } else {
                            ColorResource.subText
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            AcDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "声明", style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "本软件非 AcFun 官方产品，内部所有资源来自互联网，本应用仅作分享，目前不会保存任何用户相关信息。本软件仅做学习之用，他人如何使用此应用与本应用无关。",
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(12.dp))
            AcDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "项目地址", style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "https://github.com/succlz123/AcFun-Client-Multiplatform",
                style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(12.dp))
            AcDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "设备信息", style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = """Platform: ${getAppPlatformName()} - CPU Size: ${r.availableProcessors()}
                    
OS: ${props.getProperty("os.name")} - ${props.getProperty("os.arch")} - ${props.getProperty("os.version")}
""", style = MaterialTheme.typography.body2
            )
            Spacer(modifier = Modifier.height(12.dp))
            AcDivider()
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "版本号", style = MaterialTheme.typography.h3)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "v1.0.3", style = MaterialTheme.typography.body2)
        }
    }
}