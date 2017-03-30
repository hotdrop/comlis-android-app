package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil

class CompanyViewModel(val company: Company, val context: Context): ViewModel() {

    companion object {
        @JvmStatic private val SALARY_UNIT = "万円"
        @JvmStatic private val SALARY_RANGE_MARK = "〜"
    }

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    val viewName = company.name
    val viewEmployeesNum = company.employeesNum.toString()
    var viewSalary = company.salaryLow.toString() + SALARY_UNIT
    val viewWantedJob = company.wantedJob ?: ""

    val colorName: String
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

    /**
     * 単調だ・・なんとかいい方法はないものか
     */
    fun isOneFavorite(): Boolean {
        return viewFavorite == 1
    }

    fun isTwoFavorite(): Boolean {
        return viewFavorite == 2
    }

    fun isThreeFacorite(): Boolean {
        return viewFavorite == 3
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