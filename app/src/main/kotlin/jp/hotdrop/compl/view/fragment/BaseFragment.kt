package jp.hotdrop.compl.view.fragment

import android.support.v4.app.Fragment
import jp.hotdrop.compl.di.FragmentComponent
import jp.hotdrop.compl.di.FragmentModule
import jp.hotdrop.compl.view.activity.BaseActivity

abstract class BaseFragment: Fragment() {

    companion object {
        @JvmStatic val REFRESH_MODE = "refreshMode"
        @JvmStatic val EXTRA_COMPANY_ID = "companyId"
        @JvmStatic val EXTRA_CATEGORY_NAME = "categoryName"

        @JvmStatic val REQ_CODE_COMPANY_REGISTER = 1
        @JvmStatic val REQ_CODE_COMPANY_DETAIL = 2
        @JvmStatic val REQ_CODE_COMPANY_EDIT = 3
        @JvmStatic val REQ_CODE_COMPANY_ASSOCIATE_TAG = 4

        @JvmStatic val NONE = 0
        @JvmStatic val UPDATE = 1
        @JvmStatic val DELETE = 2
        @JvmStatic val CHANGE_CATEGORY = 4
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