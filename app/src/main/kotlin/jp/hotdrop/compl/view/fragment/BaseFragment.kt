package jp.hotdrop.compl.view.fragment

import android.support.v4.app.Fragment
import jp.hotdrop.compl.di.FragmentComponent
import jp.hotdrop.compl.di.FragmentModule
import jp.hotdrop.compl.view.activity.BaseActivity

abstract class BaseFragment: Fragment() {

    companion object {
        @JvmStatic val REFRESH_MODE = "refreshMode"

        @JvmStatic val REQ_CODE_COMPANY_REGISTER = 1
        @JvmStatic val REQ_CODE_COMPANY_DETAIL = 2
        @JvmStatic val REQ_CODE_COMPANY_EDIT = 3

        @JvmStatic val REFRESH_NONE = 0
        @JvmStatic val REFRESH = 1
    }

    private val fragmentComponent by lazy {
        val activity= activity as? BaseActivity ?: throw IllegalStateException("This fragment is not BaseActivity.")
        activity.getComponent().plus(FragmentModule(this))
    }

    fun getComponent(): FragmentComponent = fragmentComponent

    fun exit() {
        if(isResumed) {
            activity.onBackPressed()
        }
    }
}