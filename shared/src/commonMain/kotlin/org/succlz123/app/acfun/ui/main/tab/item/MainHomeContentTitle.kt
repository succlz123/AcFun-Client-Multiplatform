package org.succlz123.app.acfun.ui.main.tab.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.KeyboardArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.theme.ColorResource

@Composable
fun MainHomeContentTitle(
    modifier: Modifier, name: String, isFocused: () -> Boolean
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.background(
                if (isFocused()) {
                    ColorResource.acRed
                } else {
                    Color.Transparent
                }, shape = RoundedCornerShape(6.dp)
            ), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(12.dp, 6.dp), text = name, maxLines = 1, color = if (isFocused()) {
                    Color.White
                } else {
                    ColorResource.text
                }, style = MaterialTheme.typography.h3, fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                Icons.Sharp.KeyboardArrowRight,
                modifier = Modifier.size(16.dp),
                contentDescription = "Right",
                tint = if (isFocused()) {
                    Color.White
                } else {
                    ColorResource.text
                },
            )
            Spacer(modifier = Modifier.width(12.dp))
        }
    }
}
