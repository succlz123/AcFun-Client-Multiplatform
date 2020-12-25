package org.succlz123.lib.scroll

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs

typealias ScrollbarAdapterApp = ScrollbarAdapter

typealias ScrollbarStyleApp = ScrollbarStyle

val LocalScrollbarStyleApp: ProvidableCompositionLocal<ScrollbarStyleApp>
    get() = LocalScrollbarStyle

//@Composable
//internal fun RealVerticalScrollbar(
//    adapter: ScrollbarAdapter,
//    modifier: Modifier,
//    reverseLayout: Boolean,
//    style: ScrollbarStyle,
//    interactionSource: MutableInteractionSource
//) = androidx.compose.foundation.VerticalScrollbar(
//    adapter,
//    modifier,
//    reverseLayout,
//    style,
//    interactionSource
//)
//
//@Composable
//internal fun RealHorizontalScrollbar(
//    adapter: ScrollbarAdapter,
//    modifier: Modifier,
//    reverseLayout: Boolean,
//    style: ScrollbarStyle,
//    interactionSource: MutableInteractionSource
//) = androidx.compose.foundation.HorizontalScrollbar(
//    adapter,
//    modifier,
//    reverseLayout,
//    style,
//    interactionSource
//)
//
//@Composable
//fun rememberScrollbarAdapter(
//    scrollState: ScrollState
//): ScrollbarAdapter {
//    return androidx.compose.foundation.rememberScrollbarAdapter(scrollState)
//}
//
//@Composable
//fun rememberScrollbarAdapter(
//    scrollState: LazyListState
//): ScrollbarAdapter {
//    return androidx.compose.foundation.rememberScrollbarAdapter(scrollState)
//}

@Composable
fun rememberVerticalScrollbarAdapter(
    scrollState: LazyGridState,
    gridCells: GridCells,
    arrangement: Arrangement.Vertical?
): ScrollbarAdapterApp {
    val density = LocalDensity.current
    return remember(scrollState, gridCells, density, arrangement) {
        GridScrollbarAdapter(scrollState, gridCells, density, arrangement?.spacing ?: Dp.Hairline)
    }
}

@Composable
fun rememberHorizontalScrollbarAdapter(
    scrollState: LazyGridState,
    gridCells: GridCells,
    arrangement: Arrangement.Horizontal?
): ScrollbarAdapterApp {
    val density = LocalDensity.current
    return remember(scrollState, gridCells, density, arrangement) {
        GridScrollbarAdapter(scrollState, gridCells, density, arrangement?.spacing ?: Dp.Hairline)
    }
}

// TODO deal with item spacing
class GridScrollbarAdapter(
    private val scrollState: LazyGridState,
    private val gridCells: GridCells,
    private val density: Density,
    private val spacing: Dp
) : ScrollbarAdapterApp {
    override val scrollOffset: Float
        get() = (scrollState.firstVisibleItemIndex / itemsPerRow).coerceAtLeast(0) * averageItemSize + scrollState.firstVisibleItemScrollOffset

    override fun maxScrollOffset(containerSize: Int): Float {
        val size = with(gridCells) {
            with(density) {
                calculateCrossAxisCellSizes(containerSize, spacing.roundToPx()).size
            }
        }
        return (averageItemSize * (itemCount / size) - containerSize).coerceAtLeast(0f)
    }

    override suspend fun scrollTo(containerSize: Int, scrollOffset: Float) {
        val distance = scrollOffset - this@GridScrollbarAdapter.scrollOffset

        // if we scroll less than containerSize we need to use scrollBy function to avoid
        // undesirable scroll jumps (when an item size is different)
        //
        // if we scroll more than containerSize we should immediately jump to this position
        // without recreating all items between the current and the new position
        if (abs(distance) <= containerSize) {
            scrollState.scrollBy(distance)
        } else {
            snapTo(containerSize, scrollOffset)
        }
    }

    private suspend fun snapTo(containerSize: Int, scrollOffset: Float) {
        // In case of very big values, we can catch an overflow, so convert values to double and
        // coerce them
//        val averageItemSize = 26.000002f
//        val scrollOffsetCoerced = 2.54490608E8.toFloat()
//        val index = (scrollOffsetCoerced / averageItemSize).toInt() // 9788100
//        val offset = (scrollOffsetCoerced - index * averageItemSize) // -16.0
//        println(offset)

        val maximumValue = maxScrollOffset(containerSize).toDouble()
        val scrollOffsetCoerced = scrollOffset.toDouble().coerceIn(0.0, maximumValue)
        val averageItemSize = averageItemSize.toDouble()

        val index = (scrollOffsetCoerced / averageItemSize)
            .toInt()
            .div(
                with(gridCells) {
                    with(density) {
                        calculateCrossAxisCellSizes(containerSize, spacing.roundToPx()).size
                    }
                }
            )
            .coerceAtLeast(0)
            .coerceAtMost(itemCount - 1)

        val offset = (scrollOffsetCoerced - index * averageItemSize)
            .toInt()
            .coerceAtLeast(0)

        scrollState.scrollToItem(index = index, scrollOffset = offset)
    }

    private val itemCount get() = scrollState.layoutInfo.totalItemsCount

    private val averageItemSize: Float by derivedStateOf {
        scrollState
            .layoutInfo
            .visibleItemsInfo
            .asSequence()
            .map { it.size.height }
            .average()
            .toFloat()
    }

    private val itemsPerRow
        get() = with(gridCells) {
            with(density) {
                calculateCrossAxisCellSizes(
                    (scrollState.layoutInfo.viewportEndOffset - scrollState.layoutInfo.viewportStartOffset),
                    spacing.roundToPx()
                ).size
            }
        }
}

fun Modifier.scrollbarPadding() = padding(horizontal = 4.dp, vertical = 8.dp)