package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.databinding.ItemCompanyBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class CompanyViewModel(company: Company,
                       val context: Context,
                       val companyDao: CompanyDao,
                       val categoryDao: CategoryDao): ViewModel() {

    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)

    var id = company.id

    // 画面表示する項目はmodelとは別にフィールド値を持たせる
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
        colorName = categoryDao.find(company.categoryId).colorType
        viewFavorite = company.favorite

        viewTags = companyDao.findByTag(company.id).take(5)
    }

    fun change(vm: CompanyViewModel) {
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
        return (other as CompanyViewModel).id == id || super.equals(other)
    }

    fun onClickFirstFavorite(binding: ItemCompanyBinding) {
        if(viewFavorite == 1) {
            resetFavorite(binding)
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.reset()
            binding.animationView3.reset()
            viewFavorite = 1
            companyDao.updateFavorite(id, viewFavorite)
        }
    }

    fun onClickSecondFavorite(binding: ItemCompanyBinding) {
        if(viewFavorite == 2) {
            resetFavorite(binding)
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.playAnimation()
            binding.animationView3.reset()
            viewFavorite = 2
            companyDao.updateFavorite(id, viewFavorite)
        }
    }

    fun onClickThirdFavorite(binding: ItemCompanyBinding) {
        if(viewFavorite == 3) {
            resetFavorite(binding)
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.playAnimation()
            binding.animationView3.playAnimation()
            viewFavorite = 3
            companyDao.updateFavorite(id, viewFavorite)
        }
    }

    fun resetFavorite(binding: ItemCompanyBinding) {
        binding.animationView1.reset()
        binding.animationView2.reset()
        binding.animationView3.reset()
        viewFavorite = 0
        companyDao.updateFavorite(id, 0)
    }
}