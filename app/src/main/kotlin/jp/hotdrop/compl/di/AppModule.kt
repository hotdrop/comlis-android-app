package jp.hotdrop.compl.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class AppModule(app: Application) {

    private var context: Context = app

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }

}