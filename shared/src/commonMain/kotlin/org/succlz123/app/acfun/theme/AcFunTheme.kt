package org.succlz123.app.acfun.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorPalette = darkColors(
    primary = ColorResource.acRed, secondary = Color.LightGray
)

private val LightColorPalette = lightColors(
    primary = ColorResource.acRed, secondary = Color.Black
)

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
    MaterialTheme(
        colors = colors, typography = typography, shapes = shapes, content = content
    )
}