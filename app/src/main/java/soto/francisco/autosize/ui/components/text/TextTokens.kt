package soto.francisco.autosize.ui.components.text

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import soto.francisco.autosize.ui.components.text.TextTokens.sarif
import soto.francisco.autosize.ui.components.text.TextTokens.sarif_bold

private object TextTokens {
    val sarif = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
    )
    val sarif_bold = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
    )
}

object DSTextStyle {
    fun H1() = TextStyle(fontSize = 64.sp) + sarif_bold
    fun H2() = TextStyle(fontSize = 54.sp) + sarif_bold
    fun H3() = TextStyle(fontSize = 46.sp) + sarif_bold
    fun H4() = TextStyle(fontSize = 36.sp) + sarif_bold

    fun Body() = TextStyle(fontSize = 12.sp) + sarif

    fun P1() = TextStyle(fontSize = 20.sp) + sarif
    fun P2() = TextStyle(fontSize = 14.sp) + sarif
    fun P3() = TextStyle(fontSize = 10.sp) + sarif

}