package jp.hotdrop.compl

import android.app.Application
import jp.hotdrop.compl.dao.OrmaHolder
import jp.hotdrop.compl.di.AppComponent
import jp.hotdrop.compl.di.AppModule
import jp.hotdrop.compl.di.DaggerAppComponent

class MainApplication: Application() {

    private lateinit var appComponent: AppComponent

    @Suppress("DEPRECATION")
    override fun onCreate() {
        super.onCreate()
        // DIしたクラスが各Activityクラスで利用できるよう、Componentクラスを生成する。
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    fun getComponent(): AppComponent = appComponent
}