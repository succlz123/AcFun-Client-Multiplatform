package org.succlz123.app.acfun.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.succlz123.lib.screen.window.ScreenWindow
import org.succlz123.lib.screen.window.ScreenWindowSizeClass

private val DarkColorPalette = darkColors(
    primary = ColorResource.acRed, secondary = Color.LightGray
)

private val LightColorPalette = lightColors(
    primary = ColorResource.acRed, secondary = Color.Black
)

val LocalAppDimens = staticCompositionLocalOf { expandedDimens }

@Composable
fun AcFunTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable() () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }
    val typography = Typography(
        h1 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 22.sp
        ), h2 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 20.sp
        ), h3 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 18.sp
        ), h4 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 16.sp
        ), h5 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 14.sp
        ), h6 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Bold, fontSize = 12.sp
        ), body1 = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 14.sp
        ), body2 = TextStyle(
            fontFamily = FontFamily.Default,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = ColorResource.subText
        ), button = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.W500, fontSize = 14.sp
        ), caption = TextStyle(
            fontFamily = FontFamily.Default, fontWeight = FontWeight.Normal, fontSize = 12.sp
        ), overline = TextStyle(
            fontWeight = FontWeight.Normal, fontSize = 8.sp, letterSpacing = 1.5.sp
        )
    )
    val shapes = Shapes(
        small = RoundedCornerShape(8.dp), medium = RoundedCornerShape(12.dp), large = RoundedCornerShape(16.dp)
    )
    val sizeClass = ScreenWindow.sizeClassFlow.collectAsState()
    val dimens = when (sizeClass.value) {
        ScreenWindowSizeClass.Compact -> {
            compactDimens
        }

        ScreenWindowSizeClass.Medium -> {
            compactDimens
        }

        ScreenWindowSizeClass.Expanded -> expandedDimens
    }
    CompositionLocalProvider(
        LocalAppDimens provides dimens
    ) {
        MaterialTheme(
            colors = colors, typography = typography, shapes = shapes, content = content
        )
    }
}

class AppDimens(
    val listContentPadding: Dp
)

val compactDimens = AppDimens(
    listContentPadding = 12.dp,
)

val expandedDimens = AppDimens(
    listContentPadding = 16.dp,
)