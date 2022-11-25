package com.kitabeli.ae

import android.app.Application
import com.kitabeli.ae.di.appInitializers.AppInitializers
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KitabeliAeApp : Application() {

    @Inject
    lateinit var appInitializer: AppInitializers

    override fun onCreate() {
        super.onCreate()
        appInitializer.initialize(this)
    }
}