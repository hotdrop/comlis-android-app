package jp.hotdrop.compl.view.activity

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import jp.hotdrop.compl.MainApplication
import jp.hotdrop.compl.R
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun replaceFragment(fragment: Fragment, @IdRes @LayoutRes layoutResId: Int) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(layoutResId, fragment, fragment.javaClass.simpleName)
        ft.commit()
    }

    fun replaceFragment(fragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.setCustomAnimations(R.anim.activity_fade_enter, R.anim.activity_fade_exit)
        ft.replace(R.id.content_view, fragment, fragment.javaClass.simpleName)
        ft.addToBackStack(null)
        ft.commit()
    }
}