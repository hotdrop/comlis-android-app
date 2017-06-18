package jp.hotdrop.compl

import android.app.Application
import com.deploygate.sdk.DeployGate
import jp.hotdrop.compl.di.ApplicationComponent
import jp.hotdrop.compl.di.ApplicationModule
import jp.hotdrop.compl.di.DaggerApplicationComponent

class MainApplication: Application() {

    private lateinit var mainComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        // DIしたクラスが各Activityクラスで利用できるよう、Componentクラスを生成する。
        mainComponent = DaggerApplicationComponent.builder()
                .applicationModule(ApplicationModule(this))
                .build()

        if (!DeployGate.isInitialized()) {
            DeployGate.install(this, null, true);
        }
    }

    fun getComponent(): ApplicationComponent = mainComponent
}