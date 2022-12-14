package com.kitabeli.ae.di.modules


import com.kitabeli.ae.data.AuthenticationRepositoryImpl
import com.kitabeli.ae.data.KiosRepositoryImpl
import com.kitabeli.ae.model.repository.AuthenticationRepository
import com.kitabeli.ae.model.repository.KiosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ViewModelBinds {

    @Binds
    abstract fun bindAuthenticationRepository(impl: AuthenticationRepositoryImpl): AuthenticationRepository

    @Binds
    abstract fun bindKiosRepository(impl: KiosRepositoryImpl): KiosRepository
}