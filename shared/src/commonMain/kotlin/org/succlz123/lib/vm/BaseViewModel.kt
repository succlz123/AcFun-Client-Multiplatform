package org.succlz123.lib.vm

import androidx.compose.runtime.MutableState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.succlz123.lib.screen.result.ScreenResult
import org.succlz123.lib.screen.viewmodel.ScreenViewModel

open class BaseViewModel : ScreenViewModel() {

    fun <T> fetch(
        result: MutableStateFlow<ScreenResult<T>>,
        isForce: Boolean = false,
        isRefresh: Boolean = false,
        content: suspend () -> T?
    ) {
        if (result.value is ScreenResult.Loading) {
            return
        }
        if (!isForce && result.value is ScreenResult.Success) {
            return
        }
        if (result.value is ScreenResult.Success) {
            if (isRefresh) {
                result.value = ScreenResult.Loading()
            } else {
                result.value = ScreenResult.Loading(result.value.invoke())
            }
        } else {
            result.value = ScreenResult.Loading()
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = content()
                if (response == null) {
                    result.value = ScreenResult.Fail(NullPointerException())
                } else {
                    if (response is ArrayList<*>) {
                        if (response.isEmpty()) {
                            result.value = ScreenResult.Fail(NullPointerException())
                        } else {
                            if (isRefresh) {
                                result.value = ScreenResult.Success(response)
                            } else {
                                val before = result.value.invoke() as? ArrayList<Any>
                                val cur = if (before != null) {
                                    before
                                } else {
                                    ArrayList()
                                }
                                val responseList = response as ArrayList<*>
                                cur.addAll(responseList)
                                result.value = ScreenResult.Success(cur as T)
                            }
                        }
                    } else {
                        result.value = ScreenResult.Success(response)
                    }
                }
            } catch (e: Exception) {
                result.value = ScreenResult.Fail(e)
                e.printStackTrace()
            }
        }
    }
}