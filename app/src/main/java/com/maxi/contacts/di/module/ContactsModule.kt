package com.maxi.contacts.di.module

import android.content.Context
import com.maxi.contacts.data.repository.ContactsRepositoryImpl
import com.maxi.contacts.domain.repository.ContactsRepository
import com.maxi.contacts.utils.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class ContactsModule {

    @Provides
    fun provideContactsRepository(
        @ApplicationContext context: Context,
        dispatcherProvider: DispatcherProvider
    ): ContactsRepository =
        ContactsRepositoryImpl(context, dispatcherProvider)
}