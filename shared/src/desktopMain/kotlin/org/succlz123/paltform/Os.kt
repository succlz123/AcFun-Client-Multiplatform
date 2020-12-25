package org.succlz123.paltform

import java.util.*

fun isMac(dir: String): Boolean {
    val osName = System.getProperty("os.name", "generic")
    if (osName.lowercase(Locale.getDefault()).contains("mac")) {
        return true
    }
    return false
}

fun isLinux(): Boolean {
    val osName = System.getProperty("os.name", "generic")
    if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
        return true
    }
    return false
}

fun isWindows(): Boolean {
    val osName = System.getProperty("os.name", "generic")
    return osName.contains("indows")
}