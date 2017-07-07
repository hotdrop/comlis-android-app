package jp.hotdrop.compl.view.fragment

import android.content.res.ColorStateList
import android.support.annotation.ColorRes
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatEditText
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.deploygate.sdk.DeployGate
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import jp.hotdrop.compl.R
import jp.hotdrop.compl.di.FragmentComponent
import jp.hotdrop.compl.di.FragmentModule
import jp.hotdrop.compl.view.activity.BaseActivity

abstract class BaseFragment: Fragment() {

    companion object {
        val REFRESH_MODE = "refreshMode"
        val EXTRA_COMPANY_ID = "companyId"
        val EXTRA_CATEGORY_NAME = "categoryName"

        val NONE = 0
        val UPDATE = 1
        val DELETE = 2
        val CHANGE_CATEGORY = 4
    }

    enum class Request(val code: Int) {
        Register(1),
        Detail(2),
        AssociateTag(3),
        EditOverview(4),
        EditInformation(5),
        EditBusiness(6),
        EditJobEvaluation(7),
        EditDescription(8)
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

    fun AppCompatEditText.toText() = this.text.toString()

    fun AppCompatEditText.createNotBlankObservable(): Observable<Boolean> =
            RxTextView.textChangeEvents(this)
                    .map { it.text().isNotBlank() }
                    .distinctUntilChanged()

    fun AppCompatEditText.createEmptyOrNumberObservable(): Observable<Boolean> =
        RxTextView.textChangeEvents(this)
                .map { it.text().isEmpty() || it.text().isNumber() }
                .distinctUntilChanged()

    private fun CharSequence.isNumber(): Boolean {
        this.forEach { c -> if(!c.isDigit()) return false }
        return true
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