package com.kitabeli.ae.di.appInitializers

import android.app.Application

interface AppInitializer {
    fun initialize(application: Application)
}
