package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.model.Company
import org.parceler.Parcel

@Parcel
class CompanyViewModel() {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    var id = 0
    var name = ""
    var employeesNum = ""
    var viewSalary = ""

    constructor(company: Company) : this() {
        // Parcelはデフォルトコンストラクタを定義しないといけないため、セカンダリコンストラクタを持ってModelを保持する
        id = company.id
        name = company.name
        employeesNum = company.employeesNum.toString()
        viewSalary = company.salaryLow.toString() + SALARY_UNIT
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
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