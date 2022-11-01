package org.succlz123.app.acfun.ui.main.tab.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.api.bean.AcContent
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.lib.image.AsyncImageUrlMultiPlatform

@Composable
fun MainHomeContentInfo(
    modifier: Modifier,
    item: HomeRecommendItem,
    isFocused: () -> Boolean,
) {
    Box(
        modifier = modifier.scale(
            if (isFocused()) {
                1.05f
            } else {
                1.0f
            }
        ).clip(MaterialTheme.shapes.medium).background(
            if (isFocused()) {
                ColorResource.acRed
            } else {
                ColorResource.background
            }
        ).fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.fillMaxWidth().aspectRatio(1.82f).background(ColorResource.background),
                contentAlignment = Alignment.BottomEnd
            ) {
                AsyncImageUrlMultiPlatform(
                    url = item.item?.img.orEmpty(), modifier = Modifier.fillMaxSize()
                )
                if (item.item?.view != null && item.item?.type == AcContent.TYPE_LIVE) {
                    Text(
                        modifier = Modifier.padding(6.dp).background(ColorResource.black60, RoundedCornerShape(4.dp))
                            .padding(6.dp),
                        text = "在线：${item.item?.view.toString()}",
                        maxLines = 1,
                        color = Color.White,
                        style = MaterialTheme.typography.overline
                    )
                }
            }
            Text(
                modifier = Modifier.padding(12.dp, 12.dp, 12.dp, 0.dp),
                text = item.item?.title.orEmpty() + "\n",
                maxLines = 2,
                color = if (isFocused()) {
                    Color.White
                } else {
                    Color.Black
                },
                style = MaterialTheme.typography.h6
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.padding(12.dp, 6.dp), text = item.item?.up.orEmpty(), color = if (isFocused()) {
                        Color.White
                    } else {
                        ColorResource.subText
                    }, maxLines = 1, style = MaterialTheme.typography.body2
                )
            }
        }
    }
}
