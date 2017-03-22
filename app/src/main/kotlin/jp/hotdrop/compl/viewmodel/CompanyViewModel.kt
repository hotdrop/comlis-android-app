package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.model.Company
import org.parceler.Parcel

@Parcel
class CompanyViewModel() {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    lateinit var company: Company

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = ""
    var viewEmployeesNum = ""
    var viewSalary = ""

    /**
     * Parcelはデフォルトコンストラクタを定義しないといけないため、セカンダリコンストラクタを持ってModelを保持する
     */
    constructor(company: Company) : this() {
        this.company = company

        viewName = company.name
        viewEmployeesNum = company.employeesNum.toString()
        viewSalary = company.salaryLow.toString() + SALARY_UNIT
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