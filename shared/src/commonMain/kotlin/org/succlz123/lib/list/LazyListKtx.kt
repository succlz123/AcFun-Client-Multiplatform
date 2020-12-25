package org.succlz123.lib.list

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState

fun LazyListState.isLastItemVisible(): Boolean {
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}

fun LazyGridState.isLastItemVisible(): Boolean {
    return layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
}

fun LazyListState.isFirstItemVisible(): Boolean {
    return firstVisibleItemIndex == 0
}

fun LazyGridState.isFirstItemVisible(): Boolean {
    return firstVisibleItemIndex == 0
}

