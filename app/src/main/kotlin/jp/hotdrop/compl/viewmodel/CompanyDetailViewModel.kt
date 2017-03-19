package jp.hotdrop.compl.viewmodel

import android.view.View
import jp.hotdrop.compl.dao.CompanyDao

class CompanyDetailViewModel(companyId: Int) {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
        @JvmStatic private val EMPLOYEES_NUM_UNIT = "名"
        @JvmStatic private val EMPTY_VALUE = "未入力"
    }

    var name: String
    var employeesNum: String
    var viewSalary = ""
    var url: String? = null
    var visibleUrl: Int = View.GONE

    init {
        val company = CompanyDao.find(companyId)
        name = company.name
        employeesNum = if(company.employeesNum > 0) company.employeesNum.toString() + EMPLOYEES_NUM_UNIT else EMPTY_VALUE

        viewSalary = if(company.salaryLow > 0) company.salaryLow.toString() + SALARY_UNIT else EMPTY_VALUE
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }

        if(company.url != null) {
            url = company.url
            visibleUrl = View.VISIBLE
        }

    }
}