package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CompanyDao

class CompanyDetailViewModel(companyId: Int) {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    var name: String
    var employeesNum: String
    var viewSalary = ""

    init {
        val company = CompanyDao.find(companyId)
        name = company.name
        employeesNum = if(company.employeesNum > 0) company.employeesNum.toString() + "名" else "ー"
        viewSalary = company.salaryLow.toString() + SALARY_UNIT
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }
    }
}