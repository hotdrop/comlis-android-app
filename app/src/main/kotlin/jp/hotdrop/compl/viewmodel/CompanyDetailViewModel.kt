package jp.hotdrop.compl.viewmodel

import android.view.View
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company

class CompanyDetailViewModel(companyId: Int) {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
        @JvmStatic private val EMPLOYEES_NUM_UNIT = "名"
        @JvmStatic private val EMPTY_VALUE = "未登録"
    }

    val company: Company = CompanyDao.find(companyId)
    val viewName: String
    val viewOverview: String
    val viewEmployeesNum: String
    var viewSalary = ""
    val viewWantedJob: String
    var viewUrl: String? = null
    var visibleUrl: Int = View.GONE
    val viewNote: String

    val colorName: String

    init {
        viewName = company.name
        viewOverview = company.overview ?: EMPTY_VALUE

        viewEmployeesNum = if(company.employeesNum > 0) company.employeesNum.toString() + EMPLOYEES_NUM_UNIT else EMPTY_VALUE

        viewSalary = if(company.salaryLow > 0) company.salaryLow.toString() + SALARY_UNIT else EMPTY_VALUE
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }

        viewWantedJob = company.wantedJob ?: EMPTY_VALUE

        if(company.url != null) {
            viewUrl = company.url
            visibleUrl = View.VISIBLE
        }

        viewNote = company.note ?: EMPTY_VALUE

        colorName = CategoryDao.find(company.categoryId).colorType
    }
}