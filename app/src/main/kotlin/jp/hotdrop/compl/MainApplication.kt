package jp.hotdrop.compl

import android.app.Application
import jp.hotdrop.compl.di.AppComponent
import jp.hotdrop.compl.di.AppModule
import jp.hotdrop.compl.di.DaggerAppComponent

class MainApplication: Application() {

    // lateinitを指定すると宣言と同時に初期化しなくてもよくなる。
    private lateinit var appComponent: AppComponent

    fun getComponent(): AppComponent = appComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }
}