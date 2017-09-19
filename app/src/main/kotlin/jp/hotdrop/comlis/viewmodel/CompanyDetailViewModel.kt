package jp.hotdrop.comlis.viewmodel

import android.content.Context
import android.content.res.ColorStateList
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import io.reactivex.Completable
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.comlis.R
import jp.hotdrop.comlis.databinding.FragmentCompanyDetailBinding
import jp.hotdrop.comlis.model.Company
import jp.hotdrop.comlis.model.JobEvaluation
import jp.hotdrop.comlis.model.Tag
import jp.hotdrop.comlis.repository.category.CategoryRepository
import jp.hotdrop.comlis.repository.company.CompanyRepository
import jp.hotdrop.comlis.util.ColorUtil
import jp.hotdrop.comlis.view.parts.FavoriteStars
import javax.inject.Inject

class CompanyDetailViewModel @Inject constructor(
        private val context: Context,
        private val companyRepository: CompanyRepository,
        private val categoryRepository: CategoryRepository
): ViewModel() {

    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)
    private val EMPTY_VALUE = context.getString(R.string.label_empty_value)
    private val EMPTY_DATE = context.getString(R.string.label_empty_date)

    lateinit var binding: FragmentCompanyDetailBinding

    var id = -1
    var categoryId = -1

    var viewName = ""
    var viewOverview = ""
    var viewEmployeesNum = ""
    var viewSalary = ""
    var viewWantedJob = ""
    var viewWorkPlace = ""
    var viewUrl: String? = null
    var visibleUrl = View.GONE
    var viewDoingBusiness = ""
    var viewWantBusiness = ""
    var viewNote = ""

    private var viewFavorite = 0
    private var originalFavorite = 0

    var viewRegisterDate = ""
    var viewUpdateDate = ""

    private lateinit var jobEvaluation: JobEvaluation

    lateinit var colorName: String
    lateinit var viewTags: List<Tag>
    lateinit var favorites: FavoriteStars

    fun loadData(companyId: Int, newBinding: FragmentCompanyDetailBinding): Completable =
            companyRepository.find(companyId)
                    .toSingle()
                    .flatMapCompletable {
                        setData(it, newBinding)
                        Completable.complete()
                    }

    private fun setData(company: Company, newBinding: FragmentCompanyDetailBinding) {

        id = company.id
        categoryId = company.categoryId
        binding = newBinding

        viewName = company.name
        viewOverview = company.overview ?: EMPTY_VALUE
        viewEmployeesNum = if(company.employeesNum > 0) company.employeesNum.toString() + EMPLOYEES_NUM_UNIT else EMPTY_VALUE

        viewSalary = makeViewSalary(company.salaryLow, company.salaryHigh)

        viewWantedJob = company.wantedJob ?: EMPTY_VALUE
        viewWorkPlace = company.workPlace ?: EMPTY_VALUE

        company.url?.run {
            viewUrl = this
            visibleUrl = View.VISIBLE
        }

        viewDoingBusiness = company.doingBusiness ?: EMPTY_VALUE
        viewWantBusiness = company.wantBusiness ?: EMPTY_VALUE
        viewNote = company.note ?: EMPTY_VALUE

        viewFavorite = company.favorite

        viewRegisterDate = company.registerDate?.format() ?: EMPTY_DATE
        viewUpdateDate = company.updateDate?.format() ?: EMPTY_DATE

        // updateDate is no use
        // company.updateDate?.format() ?: EMPTY_DATE

        colorName = categoryRepository.find(categoryId).colorType
        viewTags = companyRepository.findByTag(id)

        jobEvaluation = companyRepository.findJobEvaluation(id) ?: JobEvaluation().apply { companyId = id }
    }

    private fun makeViewSalary(salaryLow: Int, salaryHigh: Int): String {
        if(salaryLow <= 0) {
            return EMPTY_VALUE
        }
        val viewSalaryLow = salaryLow.toString() + SALARY_UNIT

        if(salaryHigh <= 0) {
            return viewSalaryLow
        }
        val viewSalaryHigh = salaryHigh.toString() + SALARY_UNIT

        return viewSalaryLow + SALARY_RANGE_MARK + viewSalaryHigh
    }

    @ColorRes
    fun getColorRes() = ColorUtil.getResDark(colorName, context)

    @ColorRes
    fun getLightColorRes() = ColorUtil.getResLight(colorName, context)

    fun getCategoryName() = categoryRepository.find(categoryId).name

    fun delete() {
        companyRepository.delete(id)
    }

    private val fabOpenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_open)
    }
    private val fabCloseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_close)
    }

    private val editIconOpenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.edit_icon_open)
    }
    private val editIconCloseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.edit_icon_close)
    }

    fun initImages() {

        setImageCover(binding.imgCover)
        setEvaluation()

        binding.run {
            val darkColor = ColorUtil.getResDark(colorName, context)
            imageEditAbstract.setColorFilter(darkColor)
            imageEditInformation.setColorFilter(darkColor)
            imageEditJobEvaluation.setColorFilter(darkColor)
            imageEditBusiness.setColorFilter(darkColor)
            imageEditDescription.setColorFilter(darkColor)

            fabDetailMenu.backgroundTintList = ColorStateList.valueOf(darkColor)
            fabEdit.backgroundTintList = ColorStateList.valueOf(darkColor)
            fabTag.backgroundTintList = ColorStateList.valueOf(darkColor)
            fabTrash.backgroundTintList = ColorStateList.valueOf(darkColor)
        }
    }

    private fun setImageCover(imageView: ImageView) {
        when(colorName) {
            ColorUtil.BLUE_NAME -> imageView.setImageResource(R.drawable.blue_cover)
            ColorUtil.GREEN_NAME -> imageView.setImageResource(R.drawable.green_cover)
            ColorUtil.RED_NAME -> imageView.setImageResource(R.drawable.red_cover)
            ColorUtil.YELLOW_NAME -> imageView.setImageResource(R.drawable.yellow_cover)
            ColorUtil.PURPLE_NAME -> imageView.setImageResource(R.drawable.purple_cover)
        }
    }

    private fun setEvaluation() {
        setEvaluationColor(jobEvaluation.correctSentence, binding.jobEvalCorrectSentence)
        setEvaluationColor(jobEvaluation.developmentEnv, binding.jobEvalDevelopmentEnv)
        setEvaluationColor(jobEvaluation.wantSkill, binding.jobEvalWantSkill)
        setEvaluationColor(jobEvaluation.appeal, binding.jobEvalAppeal)
        setEvaluationColor(jobEvaluation.personImage, binding.jobEvalPersonImage)
        setEvaluationColor(jobEvaluation.jobOfferReason, binding.jobEvalOfferReason)
    }

    private val evaluationTextColor = ContextCompat.getColor(context, R.color.checked_evaluation)

    private val unCheckedColor = ContextCompat.getColor(context, R.color.unchecked_evaluation)

    private fun setEvaluationColor(checked: Boolean, v: TextView) {
        if(checked) {
            v.setTextColor(evaluationTextColor)
            v.compoundDrawableTintList = ColorStateList.valueOf(ColorUtil.getResDark(colorName, context))
        } else {
            v.setTextColor(unCheckedColor)
            v.compoundDrawableTintList = ColorStateList.valueOf(unCheckedColor)
        }
    }

    fun onClickMenuFab() {
        if(isOpenFabMenu())
            collapseFabMenu()
        else
            expandFabMenu()
    }

    fun onClickModeEditFab() {
        if(isVisibleEditFab()) {
            goneEditIcons()
            closeFabMenu()
        } else {
            visibleEditIcons()
            collapseFabMenu()
        }
    }
    private fun isVisibleEditFab() = (binding.imageEditAbstract.visibility == View.VISIBLE)

    private val FAB_MENU_OPEN_ROTATION = 90.toFloat()
    private fun expandFabMenu() {
        ViewCompat.animate(binding.fabDetailMenu)
                .rotation(FAB_MENU_OPEN_ROTATION)
                .withLayer()
                .setDuration(300)
                .setInterpolator(OvershootInterpolator(10.toFloat()))
                .start()
        binding.fabMenuTrashLayout.startAnimation(fabOpenAnimation)
        binding.fabMenuTagLayout.startAnimation(fabOpenAnimation)
        binding.fabMenuEditLayout.startAnimation(fabOpenAnimation)
        binding.fabTrash.isClickable = true
        binding.fabTag.isClickable = true
        binding.fabEdit.isClickable = true
    }

    private val FAB_MENU_CLOSE_ROTATION = 0.toFloat()
    private fun collapseFabMenu() {
        ViewCompat.animate(binding.fabDetailMenu)
                .rotation(FAB_MENU_CLOSE_ROTATION)
                .withLayer()
                .setDuration(300)
                .setInterpolator(OvershootInterpolator(10.toFloat()))
                .start()
        binding.fabMenuTrashLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuTagLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuEditLayout.startAnimation(fabCloseAnimation)
        binding.fabTrash.isClickable = false
        binding.fabTag.isClickable = false
        binding.fabEdit.isClickable = false
    }

    private fun ImageView.visibleIcon() {
        visibility = View.VISIBLE
        startAnimation(editIconOpenAnimation)
        isClickable = true
    }

    private fun ImageView.goneIcon() {
        visibility = View.GONE
        startAnimation(editIconCloseAnimation)
        isClickable = false
    }

    private fun visibleEditIcons() {
        binding.toolbarLayout.isClickable = true
        binding.imageEditAbstract.visibleIcon()
        binding.imageEditInformation.visibleIcon()
        binding.imageEditBusiness.visibleIcon()
        binding.imageEditJobEvaluation.visibleIcon()
        binding.imageEditDescription.visibleIcon()
    }

    private fun goneEditIcons() {
        binding.toolbarLayout.isClickable = false
        binding.imageEditAbstract.goneIcon()
        binding.imageEditInformation.goneIcon()
        binding.imageEditBusiness.goneIcon()
        binding.imageEditJobEvaluation.goneIcon()
        binding.imageEditDescription.goneIcon()
    }

    fun isOpenFabMenu() =
            binding.fabDetailMenu.rotation == FAB_MENU_OPEN_ROTATION

    fun closeFabMenu() {
        binding.fabMenuTrashLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuTagLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuEditLayout.startAnimation(fabCloseAnimation)
        binding.fabTrash.isClickable = false
        binding.fabTag.isClickable = false
        binding.fabEdit.isClickable = false
        binding.fabDetailMenu.rotation = 0.toFloat()
    }

    /**　ここからスターの実装 **/

    fun onClickFirstFavorite() {
        if(viewFavorite == 1) {
            resetFavorite()
        } else {
            favorites.playAnimationToOne()
            updateFavorite(1)
        }
    }

    fun onClickSecondFavorite() {
        if(viewFavorite == 2) {
            resetFavorite()
        } else {
            favorites.playAnimationToTwo()
            updateFavorite(2)
        }
    }

    fun onClickThirdFavorite() {
        if(viewFavorite == 3) {
            resetFavorite()
        } else {
            favorites.playAnimationToThree()
            updateFavorite(3)
        }
    }

    private fun resetFavorite() {
        favorites.clear()
        updateFavorite(0)
    }

    fun playFavorite() {
        favorites.playAnimation(viewFavorite)
    }

    private fun updateFavorite(favoriteNum: Int) {
        viewFavorite = favoriteNum
        companyRepository.updateFavorite(id, favoriteNum)
    }

    fun isEditFavorite() = (originalFavorite != viewFavorite)

}