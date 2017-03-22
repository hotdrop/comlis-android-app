package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.model.Company

class CompanyViewModel(var company: Company) {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = company.name
    var viewEmployeesNum = company.employeesNum.toString()
    var viewSalary = company.salaryLow.toString() + SALARY_UNIT

    init {
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other as CompanyViewModel).company.id == company.id || super.equals(other)
    }

    fun change(vm: CompanyViewModel) {
        viewName = vm.viewName
        viewEmployeesNum = vm.viewEmployeesNum
        viewSalary = vm.viewSalary
    }
}