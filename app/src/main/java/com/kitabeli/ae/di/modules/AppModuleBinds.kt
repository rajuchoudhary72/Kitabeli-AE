package com.kitabeli.ae.di.modules

import com.kitabeli.ae.data.local.SessionManager
import com.kitabeli.ae.data.local.SessionManagerImpl
import com.kitabeli.ae.di.appInitializers.AppInitializer
import com.kitabeli.ae.di.appInitializers.ThemeInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModuleBinds {

    @Binds
    @IntoSet
    abstract fun bindThemeInitializer(impl: ThemeInitializer): AppInitializer

    @Binds
    abstract fun bindSessionManager(impl: SessionManagerImpl): SessionManager


}