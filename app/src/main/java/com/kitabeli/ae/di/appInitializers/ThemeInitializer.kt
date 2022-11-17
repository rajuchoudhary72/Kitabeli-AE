package com.kitabeli.ae.di.appInitializers

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import javax.inject.Inject

class ThemeInitializer @Inject constructor() :
    AppInitializer {
    override fun initialize(application: Application) {
        // for now app only support light mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}
