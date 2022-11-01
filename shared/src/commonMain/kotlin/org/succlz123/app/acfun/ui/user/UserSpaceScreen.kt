package org.succlz123.app.acfun.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import org.succlz123.app.acfun.api.bean.HomeRecommendItem
import org.succlz123.app.acfun.base.AcBackButton
import org.succlz123.app.acfun.base.LoadingFailView
import org.succlz123.app.acfun.base.LoadingView
import org.succlz123.app.acfun.ui.main.GlobalFocusViewModel
import org.succlz123.app.acfun.ui.main.tab.item.MainHomeContentItem
import org.succlz123.lib.screen.LocalScreenNavigator
import org.succlz123.lib.screen.LocalScreenRecord
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.value
import org.succlz123.lib.screen.viewmodel.viewModel
import org.succlz123.lib.window.rememberIsWindowExpanded

@Composable
fun UserSpaceScreen() {
    val navigationScene = LocalScreenNavigator.current
    val screenRecord = LocalScreenRecord.current
    val name = screenRecord.arguments.value<String>("KEY_USER_NAME")
    val id = screenRecord.arguments.value<String>("KEY_USER_ID")
    if (name.isNullOrEmpty() || id.isNullOrEmpty()) {
        navigationScene.pop()
        return
    }
    val isExpandedScreen = rememberIsWindowExpanded()

    val viewModel = viewModel(key = id) {
        UserSpaceViewModel()
    }
    LaunchedEffect(key1 = id) {
        viewModel.loadMoreUpSpace(id)
    }

    val focusVm = viewModel(GlobalFocusViewModel::class) {
        GlobalFocusViewModel()
    }

    LaunchedEffect(Unit) {
        focusVm.curFocusRequesterParent.value = viewModel.contentFocusParent
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        val state = viewModel.userSpaceState.collectAsState().value
        when (state) {
            is ScreenResult.Uninitialized -> {
                LoadingView()
            }

            is ScreenResult.Fail -> {
                LoadingFailView(cancelClick = { navigationScene.pop() }, okClick = {
                    viewModel.loadMoreUpSpace(id)
                })
            }

            is ScreenResult.Success, is ScreenResult.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(0.dp, 48.dp, 0.dp, 0.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp, 0.dp, 24.dp, 0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AcBackButton()
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = name, style = MaterialTheme.typography.h1)
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    val acContentList = state.invoke()?.mapIndexed { index, acContent ->
                        HomeRecommendItem().apply {
                            innerItemCount = index
                            item = acContent
                        }
                    }
                    if (acContentList.isNullOrEmpty()) {
                        LoadingView()
                    } else {
                        MainHomeContentItem(result = ScreenResult.Success(acContentList.toImmutableList()),
                            isExpandedScreen = isExpandedScreen,

                            thisRequester = viewModel.contentFocusParent,
                            otherRequester = null,

                            onRefresh = {
                                viewModel.loadMoreUpSpace(id, true)
                            },
                            onLoadMore = {
                                viewModel.loadMoreUpSpace(id)
                            })
                    }
                }
            }
        }
    }
}