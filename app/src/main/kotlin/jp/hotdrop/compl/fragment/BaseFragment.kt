package jp.hotdrop.compl.fragment

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import jp.hotdrop.compl.activity.BaseActivity
import jp.hotdrop.compl.di.FragmentComponent
import jp.hotdrop.compl.di.FragmentModule

abstract class BaseFragment: Fragment() {

    val REQ_CODE_COMPANY_REGISTER: Int = 1

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