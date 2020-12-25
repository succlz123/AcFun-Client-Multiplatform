package org.succlz123.app.acfun

import android.app.Application
import org.succlz123.hohoplayer.config.PlayerConfig
import org.succlz123.hohoplayer.core.player.ext.exo.ExoMediaPlayer
import org.succlz123.lib.app.AppContentHolder

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppContentHolder.init(this)
        ExoMediaPlayer.addThis(true)
        PlayerConfig.init(this, true)
        PlayerConfig.isUseDefaultNetworkEventProducer = true
    }
}