package jp.hotdrop.compl.activity

import android.support.v7.app.AppCompatActivity
import jp.hotdrop.compl.MainApplication
import jp.hotdrop.compl.di.ActivityComponent
import jp.hotdrop.compl.di.ActivityModule

abstract class BaseActivity: AppCompatActivity() {

    private var activityComponent: ActivityComponent? = null

    fun getComponent(): ActivityComponent {
        if(activityComponent == null) {
            val mainApp = application as MainApplication
            activityComponent = mainApp.getComponent().plus(ActivityModule(this))
        }
        return activityComponent!!
    }
}