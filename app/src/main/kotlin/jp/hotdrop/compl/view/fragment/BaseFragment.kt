package jp.hotdrop.compl.view.fragment

import android.support.v4.app.Fragment
import jp.hotdrop.compl.di.FragmentComponent
import jp.hotdrop.compl.di.FragmentModule
import jp.hotdrop.compl.view.activity.BaseActivity

abstract class BaseFragment: Fragment() {

    companion object {
        @JvmStatic val REFRESH_MODE = "refreshMode"

        @JvmStatic val REQ_CODE_COMPANY_REGISTER = 1
        @JvmStatic val REQ_CODE_COMPANY_UPDATE = 2

        @JvmStatic val REFRESH_NONE = 0
        @JvmStatic val REFRESH_ONE = 1
        @JvmStatic val REFRESH_ALL = 2
    }

    private val fragmentComponent by lazy {
        val activity= activity as? BaseActivity ?: throw IllegalStateException("This fragment is not BaseActivity.")
        activity.getComponent().plus(FragmentModule(this))
    }

    fun getComponent() : FragmentComponent = fragmentComponent
}