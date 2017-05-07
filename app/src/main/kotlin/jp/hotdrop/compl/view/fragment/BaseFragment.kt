package jp.hotdrop.compl.view.fragment

import android.support.v4.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import jp.hotdrop.compl.di.FragmentComponent
import jp.hotdrop.compl.di.FragmentModule
import jp.hotdrop.compl.view.activity.BaseActivity

abstract class BaseFragment: Fragment() {

    companion object {
        val REFRESH_MODE = "refreshMode"
        val EXTRA_COMPANY_ID = "companyId"
        val EXTRA_CATEGORY_NAME = "categoryName"

        val REQ_CODE_COMPANY_REGISTER = 1
        val REQ_CODE_COMPANY_DETAIL = 2
        val REQ_CODE_COMPANY_ASSOCIATE_TAG = 3
        val REQ_CODE_COMPANY_EDIT_OVERVIEW = 4
        val REQ_CODE_COMPANY_EDIT_INFORMATION = 5
        val REQ_CODE_COMPANY_EDIT_BUSINESS = 6
        val REQ_CODE_COMPANY_EDIT_DESCRIPTION = 7

        val NONE = 0
        val UPDATE = 1
        val DELETE = 2
        val CHANGE_CATEGORY = 4
    }

    private val fragmentComponent by lazy {
        val activity= activity as? BaseActivity ?: throw IllegalStateException("This fragment is not BaseActivity.")
        activity.getComponent().plus(FragmentModule(this))
    }

    fun getComponent(): FragmentComponent = fragmentComponent

    fun LottieAnimationView.favorite(): LottieAnimationView = this.apply {
        setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak)
    }

    fun exit() {
        if(isResumed) {
            activity.onBackPressed()
        }
    }
}