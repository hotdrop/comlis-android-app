package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class CompanyViewModel(var company: Company, val context: Context): ViewModel() {

    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = company.name
    var viewEmployeesNum = company.employeesNum.toString() + EMPLOYEES_NUM_UNIT
    var viewSalary = company.salaryLow.toString() + SALARY_UNIT
    var viewWantedJob = company.wantedJob ?: ""

    var colorName: String
    var viewFavorite: Int
    var viewTags: List<Tag>

    init {
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }
        colorName = CategoryDao.find(company.categoryId).colorType
        viewFavorite = company.favorite

        viewTags = CompanyDao.findByTag(company.id).take(5)
    }

    fun change(vm: CompanyViewModel) {
        company = vm.company
        viewName = vm.viewName
        viewEmployeesNum = vm.viewEmployeesNum
        viewSalary = vm.viewSalary
        viewWantedJob = vm.viewWantedJob
        colorName = vm.colorName
        viewFavorite = vm.viewFavorite
        viewTags = vm.viewTags
    }

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResNormal(colorName, context)
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

    fun isThreeFavorite(): Boolean {
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