package org.succlz123.lib.scroll

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import org.succlz123.lib.list.isFirstItemVisible
import org.succlz123.lib.list.isLastItemVisible

data class ScrollContext(
    val isTop: Boolean,
    val isBottom: Boolean,
)

@Composable
fun rememberListScrollContext(listState: LazyListState): ScrollContext {
    val scrollContext by remember {
        derivedStateOf {
            ScrollContext(
                isTop = listState.isFirstItemVisible(),
                isBottom = listState.isLastItemVisible()
            )
        }
    }
    return scrollContext
}

@Composable
fun rememberGridScrollContext(listState: LazyGridState): ScrollContext {
    val scrollContext by remember {
        derivedStateOf {
            ScrollContext(
                isTop = listState.isFirstItemVisible(),
                isBottom = listState.isLastItemVisible()
            )
        }
    }
    return scrollContext
}