package jp.hotdrop.compl.view.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import jp.hotdrop.compl.di.FragmentComponent
import jp.hotdrop.compl.di.FragmentModule
import jp.hotdrop.compl.view.activity.BaseActivity

abstract class BaseFragment: Fragment() {

    companion object {
        @JvmStatic val REQ_CODE_COMPANY_REGISTER = 1
        @JvmStatic val REQ_CODE_COMPANY_UPDATE = 2
        @JvmStatic val REQ_CODE_CATEGORY_REGISTER = 3
        @JvmStatic val REQ_CODE_CATEGORY_UPDATE = 4

        @JvmStatic val REFRESH_MODE = "refreshMode"

        @JvmStatic val REFRESH_NONE = 0
        @JvmStatic val REFRESH_ONE = 1
        @JvmStatic val REFRESH_ALL = 2

        @JvmStatic val REFRESH_INSERT = 0
        @JvmStatic val REFRESH_UPDATE = 1
        @JvmStatic val REFRESH_DELETE = 2
    }

    var fragmentComponent: FragmentComponent? = null

    fun getComponent() : FragmentComponent {
        if(fragmentComponent != null) {
            return fragmentComponent!!
        }

        val activity: FragmentActivity = activity as? BaseActivity ?: throw IllegalStateException("This fragment is not BaseActivity.")
        fragmentComponent = (activity as BaseActivity).getComponent().plus(FragmentModule(this))
        return fragmentComponent!!
    }
}