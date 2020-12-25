package org.succlz123.app.acfun.vm

import org.succlz123.lib.vm.BaseViewModel

abstract class ScreenPageViewModel : BaseViewModel() {
    var page = 0
    var pageSize = 20

    var currentSize = 0
    var totalSize = 0

    var hasMore = true

    fun resetPageSize() {
        pageSize = 20
        page = 0
        hasMore = true
    }

    override fun clear() {
        page = 0
        hasMore = true
        onCleared()
    }
}
