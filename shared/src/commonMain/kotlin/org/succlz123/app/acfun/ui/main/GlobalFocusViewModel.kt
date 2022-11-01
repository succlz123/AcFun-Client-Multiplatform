package org.succlz123.app.acfun.ui.main

import androidx.compose.ui.focus.FocusRequester
import kotlinx.coroutines.flow.MutableStateFlow
import org.succlz123.lib.vm.BaseViewModel

class GlobalFocusViewModel : BaseViewModel() {

    val curFocusRequester = MutableStateFlow<FocusRequester?>(null)

    val curFocusRequesterParent = MutableStateFlow<FocusRequester?>(null)
}
