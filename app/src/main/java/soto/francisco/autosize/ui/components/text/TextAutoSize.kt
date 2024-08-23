package soto.francisco.autosize.ui.components.text

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.isSpecified
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import soto.francisco.autosize.ui.components.theme.Theme

/** Option 1 using onTextLayout callback
 * After the callback we can know if the text overflow.
 * If the current size is bigger than minFontSize and there is a overflow
 * we reduce the size by 0.2f triggering another recomposition.
 * @param text
 * @param minFontSize
 *
 * cons: Bad performance -> Backward rewrite (https://developer.android.com/develop/ui/compose/performance/bestpractices#avoid-backwards)
 * cons: Show an animation until it reach the desired size
 * cons: Don't work on Preview.
*/
@Composable
fun TextAutoSize_option_1(
    text: String,
    minFontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = AnnotatedString(text)
    TextAutoSize_option_1(
        text = annotatedString ,
        minFontSize = minFontSize,
        modifier= modifier,
        color= color,
        fontSize= fontSize,
        fontStyle= fontStyle,
        fontWeight= fontWeight,
        fontFamily= fontFamily,
        letterSpacing= letterSpacing,
        textDecoration= textDecoration,
        textAlign= textAlign,
        lineHeight= lineHeight,
        overflow= overflow,
        softWrap= softWrap,
        maxLines= maxLines,
        minLines= minLines,
        inlineContent= inlineContent,
        onTextLayout= onTextLayout,
        style = style,
        )
}

@Composable
fun TextAutoSize_option_1(
    text: AnnotatedString,
    minFontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val actualFontSize = fontSize.takeIf { it.isSpecified } ?: style.fontSize
    require (actualFontSize.type ==  minFontSize.type ) {
        "Calculated font size (style + fontSize) ${actualFontSize.type} should have the same type than minFontSize ${minFontSize.type}}"
    }
    var rememberedFontSize by remember(text) {
        mutableStateOf(actualFontSize)
    }

    Text(
        text = text ,
        modifier= modifier,
        color= color,
        fontSize= rememberedFontSize,
        fontStyle= fontStyle,
        fontWeight= fontWeight,
        fontFamily= fontFamily,
        letterSpacing= letterSpacing,
        textDecoration= textDecoration,
        textAlign= textAlign,
        lineHeight= lineHeight,
        overflow= overflow,
        softWrap= softWrap,
        maxLines= maxLines,
        minLines= minLines,
        inlineContent= inlineContent,
        onTextLayout= { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow && rememberedFontSize > minFontSize) {
                rememberedFontSize = if (rememberedFontSize.isEm) (rememberedFontSize.value - 0.2f).em else (rememberedFontSize.value - 0.2f).sp
            }
            onTextLayout(textLayoutResult)
        },
        style = style,
    )
}

/** Option 2 using a textMeasurer inside a BoxWithConstraints
 * Adjust the size using lerp until it fill the desired constraint or reach the minimum size.
 * @param text
 * @param minFontSize
 *
 * cons: Will require up to 100 measurements
 */
@Composable
fun TextAutoSize_option_2(
    text: String,
    minFontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = AnnotatedString(text)
    TextAutoSize_option_2(
        text = annotatedString ,
        minFontSize = minFontSize ,
        modifier= modifier ,
        color= color ,
        fontSize= fontSize ,
        fontWeight= fontWeight ,
        fontFamily= fontFamily ,
        letterSpacing= letterSpacing ,
        textDecoration= textDecoration ,
        textAlign= textAlign ,
        lineHeight= lineHeight ,
        overflow= overflow ,
        softWrap= softWrap ,
        maxLines= maxLines ,
        minLines= minLines ,
        inlineContent= inlineContent ,
        onTextLayout= onTextLayout ,
        style = style ,
    )
}

@Composable
fun TextAutoSize_option_2(
    text: AnnotatedString,
    minFontSize: TextUnit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    softWrap: Boolean = true,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (textLayoutResult: TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    BoxWithConstraints(modifier) {
        val adjustedTextStyle = calculateAdjustedTextStyle(
            text = text,
            minFontSize = minFontSize,
            constraints = constraints,
            style = style,
            fontSize = fontSize,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            inlineContent = inlineContent
        )
        Text(
            text = text,
            minLines = minLines,
            maxLines = maxLines,
            overflow = overflow,
            color = color,
            fontFamily = fontFamily,
            fontSize = fontSize,
            letterSpacing = letterSpacing,
            modifier = modifier,
            textAlign = textAlign,
            lineHeight= lineHeight,
            textDecoration = textDecoration,
            fontWeight = fontWeight,
            softWrap = softWrap,
            style = adjustedTextStyle.merge(
                TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                )
            ),
            inlineContent = inlineContent,
            onTextLayout = {
                onTextLayout(it)
            }
        )

    }
}

@Composable
private fun calculateAdjustedTextStyle(text: AnnotatedString, minFontSize: TextUnit, constraints: Constraints, fontSize: TextUnit, style: TextStyle, overflow: TextOverflow, softWrap: Boolean, maxLines: Int, inlineContent: Map<String, InlineTextContent>): TextStyle {
    val greaterTextSize = fontSize.takeIf { it.isSpecified } ?: style.fontSize
    require (greaterTextSize.type ==  minFontSize.type ) {
        "Calculated font size (style + fontSize) ${greaterTextSize.type} should have the same type than minFontSize ${minFontSize.type}}"
    }
    var step by remember {
        mutableFloatStateOf(0f)
    }
    var textStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    val measurer = rememberTextMeasurer()
    val places = text.getStringAnnotations(0,text.count())
    val placeholders = remember(key1 = inlineContent) {
        places.mapNotNull {
            if (inlineContent.keys.contains(it.item)) {
                inlineContent[it.item]?.let { content ->
                    AnnotatedString.Range(content.placeholder, it.start, it.end)
                }
            } else null
        }
    }
    return remember(text, constraints) {
        var textLayoutResult = measurer.measure(
            text = text,
            style = textStyle,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            constraints = constraints,
            placeholders = placeholders
        )
        while (!readyToDraw) {
            if (textLayoutResult.hasVisualOverflow && step <= 1f) {
                step += 0.1f
                val newSize = lerp(greaterTextSize, minFontSize, step)
                textStyle = textStyle.copy(
                    fontSize = newSize,
                )
            } else {
                readyToDraw = true
            }

            textLayoutResult = measurer.measure(
                text = text,
                style = textStyle,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                constraints = constraints,
                placeholders = placeholders
            )
        }
        textStyle
    }
}



/** Option 3 using a textMeasurer inside a BoxWithConstraints
 * Adjust the size using a binary search
 * @param text
 * @param minFontSize
 *
 */
@Composable
fun TextAutoSize_option_3(
    text: String,
    minFontSize: TextUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    val annotatedString = AnnotatedString(text)
    TextAutoSize_option_3(
        text = annotatedString ,
        minFontSize = minFontSize ,
        modifier= modifier ,
        color= color ,
        fontSize= fontSize ,
        fontWeight= fontWeight ,
        fontFamily= fontFamily ,
        letterSpacing= letterSpacing ,
        textDecoration= textDecoration ,
        textAlign= textAlign ,
        lineHeight= lineHeight ,
        overflow= overflow ,
        softWrap= softWrap ,
        maxLines= maxLines ,
        minLines= minLines ,
        inlineContent= inlineContent ,
        onTextLayout= onTextLayout ,
        style = style ,
    )
}

@Composable
fun TextAutoSize_option_3(
    text: AnnotatedString,
    minFontSize: TextUnit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    textDecoration: TextDecoration? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    softWrap: Boolean = true,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
    onTextLayout: (textLayoutResult: TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    BoxWithConstraints(modifier) {
        val adjustedTextStyle = calculateAdjustedTextStyleBinarySearch(
            text = text,
            minFontSize = minFontSize,
            constraints = constraints,
            style = style,
            fontSize = fontSize,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            inlineContent = inlineContent
        )
        Text(
            text = text,
            minLines = minLines,
            maxLines = maxLines,
            overflow = overflow,
            color = color,
            fontFamily = fontFamily,
            fontSize = fontSize,
            letterSpacing = letterSpacing,
            modifier = modifier,
            textAlign = textAlign,
            lineHeight= lineHeight,
            textDecoration = textDecoration,
            fontWeight = fontWeight,
            softWrap = softWrap,
            style = adjustedTextStyle.merge(
                TextStyle(
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                )
            ),
            inlineContent = inlineContent,
            onTextLayout = {
                onTextLayout(it)
            }
        )

    }
}

@Composable
private fun calculateAdjustedTextStyleBinarySearch(text: AnnotatedString, minFontSize: TextUnit, constraints: Constraints, fontSize: TextUnit, style: TextStyle, overflow: TextOverflow, softWrap: Boolean, maxLines: Int, inlineContent: Map<String, InlineTextContent>): TextStyle {
    val greaterTextSize = fontSize.takeIf { it.isSpecified } ?: style.fontSize
    require (greaterTextSize.type ==  minFontSize.type ) {
        "Calculated font size (style + fontSize) ${greaterTextSize.type} should have the same type than minFontSize ${minFontSize.type}}"
    }

    var lastValidSize by remember {
        mutableStateOf(minFontSize)
    }
    var topSize by remember {
        mutableStateOf(greaterTextSize)
    }
    var bottomSize by remember {
        mutableStateOf(minFontSize)
    }

    fun midPointSize() = if (bottomSize.isSp) {
        ((bottomSize.value + topSize.value) / 2).sp
    } else {
        ((bottomSize.value + topSize.value) / 2).em
    }
    var textStyle by remember { mutableStateOf(style) }
    var readyToDraw by remember { mutableStateOf(false) }
    val measurer = rememberTextMeasurer()
    val placeholders = remember(inlineContent) {
        val places = text.getStringAnnotations(0, text.count())
        places.mapNotNull {
            if (inlineContent.keys.contains(it.item)) {
                inlineContent[it.item]?.let { content ->
                    AnnotatedString.Range(content.placeholder, it.start, it.end)
                }
            } else null
        }
    }

    return remember(text, constraints) {
        var textLayoutResult = measurer.measure(
            text = text,
            style = textStyle,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            constraints = constraints,
            placeholders = placeholders
        )
        while (!readyToDraw) {
            if (bottomSize >= topSize) {
                readyToDraw = true
                textStyle = textStyle.copy(
                    fontSize = lastValidSize
                )
            } else if (textLayoutResult.hasVisualOverflow) {
                topSize = midPointSize()
                textStyle = textStyle.copy(
                    fontSize = topSize
                )
            } else if (!textLayoutResult.hasVisualOverflow) {
                lastValidSize = textStyle.fontSize
                bottomSize = midPointSize()
                textStyle = textStyle.copy(
                    fontSize = bottomSize
                )
            }

            textLayoutResult = measurer.measure(
                text = text,
                style = textStyle,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                constraints = constraints,
                placeholders = placeholders
            )
        }
        textStyle
    }
}

@Preview
@Composable
fun PreviewOption1() {
    Column(
        Modifier
            .background(Theme.Colors.Surface.colorPrimary)
            .size(150.dp)) {
        TextAutoSize_option_1(text = LoremIpsum(4).values.joinToString(" "), maxLines = 1, minFontSize = 10.sp, style = DSTextStyle.H2(), color = Theme.Colors.Item.colorPrimary)
    }
}

@Preview
@Composable
fun PreviewOption2() {
    Column(
        Modifier
            .background(Theme.Colors.Surface.colorPrimary)
            .size(150.dp)) {
        TextAutoSize_option_2(text = LoremIpsum(6).values.joinToString(" "), maxLines = 1, minFontSize = 8.sp, style = DSTextStyle.H2(), color = Theme.Colors.Item.colorPrimary)
    }
}

@Preview
@Composable
fun PreviewOption3() {
    Column(
        Modifier
            .background(Theme.Colors.Surface.colorPrimary)
            .size(150.dp)) {
        TextAutoSize_option_3(text = LoremIpsum(6).values.joinToString(" "), maxLines = 1, minFontSize = 8.sp, style = DSTextStyle.H2(), color = Theme.Colors.Item.colorPrimary)
    }
}