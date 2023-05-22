package com.kitabeli.ae

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.kitabeli.ae.di.appInitializers.AppInitializers
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KitabeliAeApp : Application() {
    @Inject
    lateinit var appInitializer: AppInitializers
    override fun onCreate() {
        super.onCreate()
        initFireBaseRemoteConfig()
        appInitializer.initialize(this)


    }

    private fun initFireBaseRemoteConfig() {
        FirebaseApp.initializeApp(this)
        FirebaseRemoteConfig.getInstance().apply {
            //set this during development
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build()
            setConfigSettingsAsync(configSettings)
            //set this during development

            setDefaultsAsync(R.xml.remote_config_default)
            fetchAndActivate().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                } else {
                }
            }
        }
    }
}