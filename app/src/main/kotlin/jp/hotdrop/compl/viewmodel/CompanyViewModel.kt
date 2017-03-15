package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.model.Company

class CompanyViewModel(company: Company) {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    var id = company.id
    var name = company.name
    var employeesNum = company.employeesNum.toString()
    var viewSalary: String

    init {
        if(company.salaryHigh <= 0) {
            viewSalary = company.salaryLow.toString() + SALARY_UNIT
        } else {
            viewSalary = company.salaryLow.toString() + SALARY_UNIT + SALARY_RANGE_MARK +
                         company.salaryHigh.toString() + SALARY_UNIT
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as CompanyViewModel).id == id || super.equals(other)
    }

    fun change(vm: CompanyViewModel) {
        id = vm.id
        name = vm.name
        employeesNum = vm.employeesNum
        viewSalary = vm.viewSalary
    }
}