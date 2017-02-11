package jp.hotdrop.compl

import android.app.Application

import jp.hotdrop.compl.di.AppComponent


class MainApplication : Application() {

    var appComponent: AppComponent? = null

    fun getComponent(): AppComponent? {
        return appComponent
    }

    override fun onCreate() {
        super.onCreate()

    }
}