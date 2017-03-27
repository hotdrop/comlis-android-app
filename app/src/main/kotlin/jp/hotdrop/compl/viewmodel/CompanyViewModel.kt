package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil

class CompanyViewModel(var company: Company, val context: Context) {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = company.name
    var viewEmployeesNum = company.employeesNum.toString()
    var viewSalary = company.salaryLow.toString() + SALARY_UNIT
    var viewWantedJob = company.wantedJob ?: ""

    var colorName: String
    var viewFavorite: Int

    init {
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }
        colorName = CategoryDao.find(company.categoryId).colorType
        viewFavorite = company.favorite
    }

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResLight(colorName, context)
    }

    override fun equals(other: Any?): Boolean {
        return (other as CompanyViewModel).company.id == company.id || super.equals(other)
    }

    fun tapFavorite(tapCnt: Int) {
        CompanyDao.updateFavorite(company.id, tapCnt)
        viewFavorite = tapCnt
    }

    fun resetFavorite() {
        viewFavorite = 0
        CompanyDao.updateFavorite(company.id, 0)
    }
}