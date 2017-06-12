package jp.hotdrop.compl.viewmodel

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
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.databinding.FragmentCompanyDetailBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil
import javax.inject.Inject

class CompanyDetailViewModel @Inject constructor(val context: Context): ViewModel() {

    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)
    private val EMPTY_VALUE = context.getString(R.string.label_empty_value)
    private val EMPTY_DATE = context.getString(R.string.label_empty_date)

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var jobEvaluationDao: JobEvaluationDao

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
    var viewFavorite = 0
    var originalFavorite = 0
    var viewRegisterDate = ""
    var viewUpdateDate = ""

    lateinit var jobEvaluation: JobEvaluation
    lateinit var colorName: String
    lateinit var viewTags: List<Tag>

    fun loadData(companyId: Int, newBinding: FragmentCompanyDetailBinding): Completable {
        return companyDao.findObserve(companyId)
                .flatMapCompletable { company ->
                    setData(company, newBinding)
                    Completable.complete()
                }
    }

    private fun setData(company: Company, newBinding: FragmentCompanyDetailBinding) {

        id = company.id
        categoryId = company.categoryId
        binding = newBinding

        viewName = company.name
        viewOverview = company.overview ?: EMPTY_VALUE
        viewEmployeesNum = if(company.employeesNum > 0) company.employeesNum.toString() + EMPLOYEES_NUM_UNIT else EMPTY_VALUE
        viewSalary = if(company.salaryLow > 0) company.salaryLow.toString() + SALARY_UNIT else EMPTY_VALUE
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }
        viewWantedJob = company.wantedJob ?: EMPTY_VALUE
        viewWorkPlace = company.workPlace ?: EMPTY_VALUE
        company.url?.let {
            viewUrl = it
            visibleUrl = View.VISIBLE
        }
        viewDoingBusiness = company.doingBusiness ?: EMPTY_VALUE
        viewWantBusiness = company.wantBusiness ?: EMPTY_VALUE
        viewNote = company.note ?: EMPTY_VALUE
        viewFavorite = company.favorite
        viewRegisterDate = company.registerDate?.format() ?: EMPTY_DATE
        viewUpdateDate = company.updateDate?.format() ?: EMPTY_DATE

        colorName = categoryDao.find(categoryId).colorType
        viewTags = companyDao.findByTag(id)

        jobEvaluation = jobEvaluationDao.find(id) ?: JobEvaluation().apply { companyId = id }
    }

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResDark(colorName, context)

    @ColorRes
    fun getLightColorRes(): Int = ColorUtil.getResLight(colorName, context)

    fun getCategoryName(): String = categoryDao.find(categoryId).name

    fun delete() {
        companyDao.delete(id)
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

        binding.let {
            val darkColor = ColorUtil.getResDark(colorName, context)
            it.imageEditAbstract.setColorFilter(darkColor)
            it.imageEditInformation.setColorFilter(darkColor)
            it.imageEditJobEvaluation.setColorFilter(darkColor)
            it.imageEditBusiness.setColorFilter(darkColor)
            it.imageEditDescription.setColorFilter(darkColor)

            it.fabDetailMenu.backgroundTintList = ColorStateList.valueOf(darkColor)
            it.fabEdit.backgroundTintList = ColorStateList.valueOf(darkColor)
            it.fabTag.backgroundTintList = ColorStateList.valueOf(darkColor)
            it.fabTrash.backgroundTintList = ColorStateList.valueOf(darkColor)
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
        if(isOpenFabMenu()) collapseFabMenu() else expandFabMenu()
    }

    fun onClickModeEditFab() {
        if(binding.imageEditAbstract.visibility == View.VISIBLE) {
            goneEditIcons()
            closeFabMenu()
        } else {
            visibleEditIcons()
            collapseFabMenu()
        }
    }

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

    private fun collapseFabMenu() {
        ViewCompat.animate(binding.fabDetailMenu)
                .rotation(0.toFloat())
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

    private fun ImageView.visibleIcon(): Unit {
        visibility = View.VISIBLE
        startAnimation(editIconOpenAnimation)
        isClickable = true
    }

    private fun ImageView.goneIcon(): Unit {
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

    fun isOpenFabMenu(): Boolean = binding.fabDetailMenu.rotation == FAB_MENU_OPEN_ROTATION

    fun closeFabMenu() {
        binding.fabMenuTrashLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuTagLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuEditLayout.startAnimation(fabCloseAnimation)
        binding.fabTrash.isClickable = false
        binding.fabTag.isClickable = false
        binding.fabEdit.isClickable = false
        binding.fabDetailMenu.rotation = 0.toFloat()
    }

    fun onClickFirstFavorite() {
        if(viewFavorite == 1) {
            resetFavorite()
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.reset()
            binding.animationView3.reset()
            viewFavorite = 1
            companyDao.updateFavorite(id, viewFavorite)
        }
    }

    fun onClickSecondFavorite() {
        if(viewFavorite == 2) {
            resetFavorite()
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.playAnimation()
            binding.animationView3.reset()
            viewFavorite = 2
            companyDao.updateFavorite(id, viewFavorite)
        }
    }

    fun onClickThirdFavorite() {
        if(viewFavorite == 3) {
            resetFavorite()
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.playAnimation()
            binding.animationView3.playAnimation()
            viewFavorite = 3
            companyDao.updateFavorite(id, viewFavorite)
        }
    }

    fun isEditFavorite(): Boolean {
        return (originalFavorite != viewFavorite)
    }

    private fun resetFavorite() {
        binding.animationView1.reset()
        binding.animationView2.reset()
        binding.animationView3.reset()
        viewFavorite = 0
        companyDao.updateFavorite(id, 0)
    }
}