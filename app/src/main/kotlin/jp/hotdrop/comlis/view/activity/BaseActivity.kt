package jp.hotdrop.comlis.view.activity

import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import jp.hotdrop.comlis.MainApplication
import jp.hotdrop.comlis.di.ActivityComponent
import jp.hotdrop.comlis.di.ActivityModule

abstract class BaseActivity: AppCompatActivity() {

    private val activityComponent by lazy {
        (application as MainApplication).getComponent().plus(ActivityModule(this))
    }

    fun getComponent(): ActivityComponent = activityComponent

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun replaceFragment(fragment: Fragment, @IdRes @LayoutRes layoutResId: Int) {
        supportFragmentManager.beginTransaction()
                .replace(layoutResId, fragment, fragment.javaClass.simpleName)
                .commit()
    }
}