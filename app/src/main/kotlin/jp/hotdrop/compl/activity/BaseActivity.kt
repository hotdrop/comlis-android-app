package jp.hotdrop.compl.activity

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
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

    fun replaceFragment(fragment: Fragment, @IdRes @LayoutRes layoutResId: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(layoutResId, fragment, fragment.javaClass.simpleName)
        ft.commit()
    }
}