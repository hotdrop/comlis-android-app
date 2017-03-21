package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.model.Company
import org.parceler.Parcel

@Parcel
class CompanyViewModel() {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    var viewId = 0
    var viewName = ""
    var viewEmployeesNum = ""
    var viewSalary = ""
    var viewOrder = 0

    constructor(company: Company) : this() {
        // Parcelはデフォルトコンストラクタを定義しないといけないため、セカンダリコンストラクタを持ってModelを保持する
        viewId = company.id
        viewName = company.name
        viewEmployeesNum = company.employeesNum.toString()
        viewSalary = company.salaryLow.toString() + SALARY_UNIT
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }
        viewOrder = company.order
    }

    override fun equals(other: Any?): Boolean {
        return (other as CompanyViewModel).viewId == viewId || super.equals(other)
    }

    fun change(vm: CompanyViewModel) {
        viewId = vm.viewId
        viewName = vm.viewName
        viewEmployeesNum = vm.viewEmployeesNum
        viewSalary = vm.viewSalary
        viewOrder = vm.viewOrder
    }
}