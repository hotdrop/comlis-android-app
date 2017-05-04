package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import io.reactivex.Completable
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.databinding.FragmentCompanyDetailBinding
import jp.hotdrop.compl.model.Company
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
    }

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResDark(colorName, context)
    }

    @ColorRes
    fun getLightColorRes(): Int {
        return ColorUtil.getResLight(colorName, context)
    }

    fun getCategoryName(): String {
        return categoryDao.find(categoryId).name
    }

    fun delete() {
        companyDao.delete(id)
    }

    private val fabOpenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_open)
    }
    private val fabCloseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.fab_close)
    }
    private var isFabMenuOpen = false

    private val editIconOpenAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.edit_icon_open)
    }
    private val editIconCloseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.edit_icon_close)
    }
    private var isModeEdit = false

    fun onClickMenuFab() {
        if(isFabMenuOpen) collapseFabMenu() else expandFabMenu()
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

    private fun expandFabMenu() {
        ViewCompat.animate(binding.fabDetailMenu)
                .rotation(90.toFloat())
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
        isFabMenuOpen = true
    }

    private fun collapseFabMenu() {
        ViewCompat.animate(binding.fabDetailMenu).rotation(0.toFloat())
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
        isFabMenuOpen = false
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
        binding.imageEditDescription.visibleIcon()
        isModeEdit = true
    }

    private fun goneEditIcons() {
        binding.toolbarLayout.isClickable = false
        binding.imageEditAbstract.goneIcon()
        binding.imageEditInformation.goneIcon()
        binding.imageEditBusiness.goneIcon()
        binding.imageEditDescription.goneIcon()
        isModeEdit = false
    }

    fun isOpenFabMenu(): Boolean {
        return isFabMenuOpen
    }

    fun closeFabMenu() {
        binding.fabMenuTrashLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuTagLayout.startAnimation(fabCloseAnimation)
        binding.fabMenuEditLayout.startAnimation(fabCloseAnimation)
        binding.fabTrash.isClickable = false
        binding.fabTag.isClickable = false
        binding.fabEdit.isClickable = false
        isFabMenuOpen = false
        binding.fabDetailMenu.rotation = 0.toFloat()
    }

    private val RESET = 0.toFloat()
    fun onClickFirstFavorite() {
        if(viewFavorite == 1) {
            resetFavorite()
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.progress = RESET
            binding.animationView3.progress = RESET
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
            binding.animationView3.progress = RESET
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
        binding.animationView1.progress = RESET
        binding.animationView2.progress = RESET
        binding.animationView3.progress = RESET
        viewFavorite = 0
        companyDao.updateFavorite(id, 0)
    }
}