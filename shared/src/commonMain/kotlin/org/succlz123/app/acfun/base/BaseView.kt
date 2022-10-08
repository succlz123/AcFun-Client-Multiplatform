package org.succlz123.app.acfun.base

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.outlinedButtonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBack
import androidx.compose.material.icons.sharp.KeyboardArrowUp
import androidx.compose.material.icons.sharp.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.succlz123.app.acfun.theme.ColorResource
import org.succlz123.lib.click.noRippleClickable
import org.succlz123.lib.screen.LocalScreenNavigator

@Composable
fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp), color = ColorResource.acRed, strokeWidth = 6.dp
        )
    }
}

@Composable
fun AcRefreshButton(modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, backgroundColor = Color.White, elevation = 12.dp) {
        Box(modifier = Modifier.padding(12.dp).noRippleClickable(onClick)) {
            Icon(
                Icons.Sharp.Refresh,
                modifier = Modifier.size(36.dp).padding(2.dp),
                contentDescription = "Refresh",
                tint = ColorResource.acRed
            )
        }
    }
}

@Composable
fun AcGo2TopButton(modifier: Modifier, onClick: () -> Unit) {
    Card(modifier = modifier, backgroundColor = Color.White, elevation = 12.dp) {
        Box(modifier = Modifier.padding(12.dp).noRippleClickable(onClick)) {
            Icon(
                Icons.Sharp.KeyboardArrowUp,
                modifier = Modifier.size(36.dp).padding(2.dp),
                contentDescription = "Go to top",
                tint = ColorResource.acRed
            )
        }
    }
}

@Composable
fun AcBackButton(modifier: Modifier = Modifier, tint: Color = Color.Black, onClick: (() -> Boolean)? = null) {
    val navigationScene = LocalScreenNavigator.current
    Icon(
        Icons.Sharp.ArrowBack, modifier = modifier.size(32.dp).noRippleClickable {
            if (onClick?.invoke() == true) {
            } else {
                navigationScene.pop()
            }
        }, contentDescription = "Back", tint = tint
    )
}

@Composable
fun AcDivider() {
    Spacer(modifier = Modifier.fillMaxWidth().height(1.dp).background(ColorResource.divider))
}

@Composable
fun AcButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Button(
        colors = ButtonDefaults.outlinedButtonColors(
            backgroundColor = ColorResource.acRed, contentColor = Color.White, disabledContentColor = Color.Transparent
        ), contentPadding = PaddingValues(
            start = 18.dp, top = 4.dp, end = 18.dp, bottom = 4.dp
        ), onClick = onClick
    ) {
        content.invoke()
    }
}

@Composable
fun AcOutlineButton(modifier: Modifier, onClick: () -> Unit, content: @Composable () -> Unit) {
    OutlinedButton(
        modifier = modifier, onClick = onClick, border = BorderStroke(
            ButtonDefaults.OutlinedBorderSize, ColorResource.acRed
        ), colors = outlinedButtonColors(
            backgroundColor = Color.Transparent,
            contentColor = ColorResource.acRed,
            disabledContentColor = Color.Transparent
        )
    ) {
        content.invoke()
    }
}