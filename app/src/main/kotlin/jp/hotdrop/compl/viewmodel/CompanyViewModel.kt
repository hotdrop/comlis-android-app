package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorDataUtil

class CompanyViewModel(var company: Company, val context: Context) {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = company.name
    var viewEmployeesNum = company.employeesNum.toString()
    var viewSalary = company.salaryLow.toString() + SALARY_UNIT
    var colorName: String

    init {
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }
        colorName = CategoryDao.find(company.categoryId).colorType
    }

    @ColorRes
    fun getColorRes(): Int {
        return ContextCompat.getColor(context, ColorDataUtil.getColorLight(colorName))
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