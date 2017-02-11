package jp.hotdrop.compl.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class AppModule @Inject constructor(val application: Application) {

    @Provides
    fun provideContext(): Context = application.applicationContext
}