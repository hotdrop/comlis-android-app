package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.view.View
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class CompanyDetailViewModel (companyId: Int, val context: Context,
                             val companyDao: CompanyDao, val categoryDao: CategoryDao, val tagDao: TagDao): ViewModel() {

    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)
    private val EMPTY_VALUE = context.getString(R.string.label_empty_value)
    private val EMPTY_DATE = context.getString(R.string.label_empty_date)

    val company: Company = companyDao.find(companyId)
    val viewName: String
    val viewOverview: String
    val viewEmployeesNum: String
    var viewSalary = ""
    val viewWantedJob: String
    var viewWorkPlace = ""
    var viewUrl: String? = null
    var visibleUrl: Int = View.GONE

    var viewDoingBusiness = ""
    var viewWantBusiness = ""
    val viewNote: String

    var viewFavorite: Int

    val viewRegisterDate: String
    val viewUpdateDate: String

    val colorName: String

    val viewTags: List<Tag>
    val canAttachTag = if(tagDao.count() > 0) View.VISIBLE else View.GONE

    init {
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

        colorName = categoryDao.find(company.categoryId).colorType

        viewTags = companyDao.findByTag(company.id)
    }

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResDark(colorName, context)
    }

    fun isOneFavorite(): Boolean {
        return viewFavorite == 1
    }

    fun isTwoFavorite(): Boolean {
        return viewFavorite == 2
    }

    fun isThreeFavorite(): Boolean {
        return viewFavorite == 3
    }

    fun tapFavorite(tapCnt: Int) {
        companyDao.updateFavorite(company.id, tapCnt)
        viewFavorite = tapCnt
    }

    fun resetFavorite() {
        viewFavorite = 0
        companyDao.updateFavorite(company.id, 0)
    }

    fun getCategoryName(): String {
        val category = categoryDao.find(company.categoryId)
        return category.name
    }
}