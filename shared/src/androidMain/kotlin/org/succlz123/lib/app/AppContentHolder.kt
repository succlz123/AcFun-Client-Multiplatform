package org.succlz123.lib.app

import android.app.Application

object AppContentHolder {

    private lateinit var appContext: Application

    fun init(app: Application) {
        appContext = app
    }

    fun get(): Application {
        return appContext
    }
}