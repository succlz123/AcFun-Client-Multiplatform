package org.succlz123.lib.setting

import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.succlz123.lib.app.AppContentHolder

actual fun createSettings(name: String): Settings {
    val sp = AppContentHolder.get().getSharedPreferences(name, Context.MODE_PRIVATE)
    return SharedPreferencesSettings(sp)
}