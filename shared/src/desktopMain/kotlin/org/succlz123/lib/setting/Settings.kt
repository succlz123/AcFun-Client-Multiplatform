package org.succlz123.lib.setting

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

class Settings {

}

actual fun createSettings(name: String): Settings {
    val preferences = Preferences.userNodeForPackage(Settings::class.java)
//    val preferences = Preferences.userRoot()
    return PreferencesSettings(preferences)
}