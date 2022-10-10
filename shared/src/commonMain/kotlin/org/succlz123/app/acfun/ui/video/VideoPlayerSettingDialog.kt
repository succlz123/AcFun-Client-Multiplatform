package org.succlz123.app.acfun.ui.video

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.viewmodel.sharedViewModel
import org.succlz123.lib.screen.window.ScreenWindow
import org.succlz123.lib.video.PlayerSetting
import org.succlz123.lib.video.VideoPlayerViewModel
import org.succlz123.lib.window.rememberIsWindowExpanded

@Composable
fun VideoPlayerSettingDialog() {
    val navigationScene = LocalScreenNavigator.current
    val density = LocalDensity.current
    val screenSize = ScreenWindow.sizeFlow.collectAsState()

    val playerViewModel = sharedViewModel {
        VideoPlayerViewModel()
    }
    val isExpandedScreen = rememberIsWindowExpanded()
    val gridCellSize = remember(isExpandedScreen) {
        if (isExpandedScreen) {
            5
        } else {
            2
        }
    }
    Box(modifier = Modifier.fillMaxSize().noRippleClickable {
        navigationScene.pop()
    }, contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.width(((screenSize.value.width / 5 * 4) / density.density).dp)
                .align(Alignment.Center), elevation = 3.dp, backgroundColor = Color.White
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.width(52.dp),
                        text = "倍速:",
                        style = MaterialTheme.typography.h4,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridCellSize),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(PlayerSetting.speedTags) { index, item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(32.dp))
                                Text(
                                    modifier = Modifier.noRippleClickable {
                                        playerViewModel.playerSetting.speed.value = item
                                        navigationScene.pop()
                                    },
                                    text = item,
                                    style = if (item == playerViewModel.playerSetting.speed.value) {
                                        MaterialTheme.typography.h3
                                    } else {
                                        MaterialTheme.typography.h5
                                    },
                                    color = if (item == playerViewModel.playerSetting.speed.value) {
                                        ColorResource.acRed
                                    } else {
                                        ColorResource.subText
                                    },
                                    fontWeight = if (item == playerViewModel.playerSetting.speed.value) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.width(52.dp),
                        text = "比例:",
                        style = MaterialTheme.typography.h4,
                        color = Color.Black,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(gridCellSize),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        itemsIndexed(PlayerSetting.ratioTags) { index, item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(32.dp))
                                Text(
                                    modifier = Modifier.noRippleClickable {
                                        playerViewModel.playerSetting.ratio.value = item
                                        navigationScene.pop()
                                    },
                                    text = item,
                                    style = if (item == playerViewModel.playerSetting.ratio.value) {
                                        MaterialTheme.typography.h3
                                    } else {
                                        MaterialTheme.typography.h5
                                    },
                                    color = if (item == playerViewModel.playerSetting.ratio.value) {
                                        ColorResource.acRed
                                    } else {
                                        ColorResource.subText
                                    },
                                    fontWeight = if (item == playerViewModel.playerSetting.ratio.value) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    },
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
                if (!playerViewModel.playList.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.width(52.dp),
                            text = "清晰度:",
                            style = MaterialTheme.typography.h4,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(gridCellSize),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            itemsIndexed(playerViewModel.playList.orEmpty()) { index, item ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Spacer(modifier = Modifier.width(32.dp))
                                    Text(
                                        modifier = Modifier.noRippleClickable {
                                            playerViewModel.curPlayerSource.value = item
                                            navigationScene.pop()
                                        },
                                        text = item.qualityLabel.orEmpty(),
                                        style = if (item == playerViewModel.curPlayerSource.value) {
                                            MaterialTheme.typography.h3
                                        } else {
                                            MaterialTheme.typography.h5
                                        },
                                        color = if (item == playerViewModel.curPlayerSource.value) {
                                            ColorResource.acRed
                                        } else {
                                            ColorResource.subText
                                        },
                                        fontWeight = if (item == playerViewModel.curPlayerSource.value) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Normal
                                        },
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            modifier = Modifier.width(52.dp),
                            text = "弹幕:",
                            style = MaterialTheme.typography.h4,
                            color = Color.Black,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                        PlayerSetting.danmakuTags.forEach {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(modifier = Modifier.width(32.dp))
                                val isSelect =
                                    (it == "开启" && playerViewModel.playerSetting.danmaku.value) || (it == "关闭" && !playerViewModel.playerSetting.danmaku.value)
                                Text(
                                    modifier = Modifier.noRippleClickable {
                                        playerViewModel.playerSetting.danmaku.value = it == "开启"
                                        navigationScene.pop()
                                    }, text = it, style = if (isSelect) {
                                        MaterialTheme.typography.h3
                                    } else {
                                        MaterialTheme.typography.h5
                                    }, color = if (isSelect) {
                                        ColorResource.acRed
                                    } else {
                                        ColorResource.subText
                                    }, fontWeight = if (isSelect) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Normal
                                    }, maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}