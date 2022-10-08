package org.succlz123.app.acfun.ui.main.tab.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.base.AcDivider

@Composable
fun MainRightTitleLayout(
    modifier: Modifier = Modifier,
    text: String,
    topRightContent: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.background(Color.White)) {
        Row(
            modifier = Modifier.padding(26.dp, 12.dp, 26.dp, 12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = text, style = MaterialTheme.typography.h1)
            Spacer(modifier = Modifier.height(1.dp).weight(1f))
            topRightContent?.invoke()
        }
        AcDivider()
        content()
    }
}