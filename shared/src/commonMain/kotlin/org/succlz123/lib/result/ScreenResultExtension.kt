package org.succlz123.lib.result

import org.succlz123.lib.screen.result.ScreenResult

fun <T> screenResultDataNone(msg: String? = null) = ScreenResult.Fail<T>(NullPointerException(msg))