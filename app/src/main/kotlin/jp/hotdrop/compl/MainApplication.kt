package jp.hotdrop.compl

import android.app.Application
import jp.hotdrop.compl.di.AppComponent
import jp.hotdrop.compl.di.AppModule
import jp.hotdrop.compl.di.DaggerAppComponent


class MainApplication : Application() {

    companion object {
        @JvmStatic lateinit var appComponent: AppComponent
    }

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .build()
    }
}