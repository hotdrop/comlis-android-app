package jp.hotdrop.compl.view.fragment

import android.content.res.ColorStateList
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.deploygate.sdk.DeployGate
import jp.hotdrop.compl.R
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

    fun LottieAnimationView.setFavoriteStar(): LottieAnimationView = this.apply {
        setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak)
    }

    fun AppCompatButton.enabledWithColor(@ColorRes res: Int) {
        this.isEnabled = true
        this.backgroundTintList = ColorStateList.valueOf(res)
    }

    fun AppCompatButton.disabledWithColor() {
        this.isEnabled = false
        this.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.button_disabled))
    }

    sealed class ErrorType {
        object LoadFailureCompanies: ErrorType()
        object LoadFailureCompany: ErrorType()
        object LoadFailureCategory: ErrorType()
        object LoadFailureTags: ErrorType()
        object LoadFailureSearch: ErrorType()
    }

    fun showErrorAsToast(type: ErrorType, e: Throwable) {
        val msg = when(type) {
            ErrorType.LoadFailureCompanies -> context.getString(R.string.load_failure_companies)
            ErrorType.LoadFailureCompany -> context.getString(R.string.load_failure_company)
            ErrorType.LoadFailureCategory -> context.getString(R.string.load_failure_categories)
            ErrorType.LoadFailureTags -> context.getString(R.string.load_failure_tags)
            ErrorType.LoadFailureSearch -> context.getString(R.string.load_failure_search)
        }
        // DeployGateに流す処理、本当はビルドタイプを分けてデバッグに記述すべき。そもそもbuild.gradleに・・
        DeployGate.logError(msg + " throwable message =" + e.message)
        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
    }

    fun exit() {
        if(isResumed) {
            activity.onBackPressed()
        }
    }
}