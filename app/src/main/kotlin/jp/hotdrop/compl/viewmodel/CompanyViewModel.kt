package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.databinding.ItemCompanyBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class CompanyViewModel(company: Company,
                       private val context: Context,
                       private val companyDao: CompanyDao,
                       private val categoryDao: CategoryDao,
                       private val jobEvaluationDao: JobEvaluationDao): ViewModel() {

    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)
    private val JOB_EVALUATION_UNIT = context.getString(R.string.label_job_evaluation_unit)

    var id = -1

    // 画面表示する項目はmodelとは別にフィールド値を持たせる
    var viewName = ""
    var viewWantedJob = ""
    var viewJobEvaluation = "0" + JOB_EVALUATION_UNIT
    var viewEmployeesNum = ""
    var viewSalary = ""
    var viewFavorite = 0

    lateinit var viewTags: List<Tag>
    lateinit var colorName: String

    init {
        setData(company)
    }

    fun setData(company: Company) {
        id = company.id

        viewName = company.name
        viewWantedJob = company.wantedJob ?: ""

        viewEmployeesNum = company.employeesNum.toString() + EMPLOYEES_NUM_UNIT
        viewSalary = company.salaryLow.toString() + SALARY_UNIT
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }

        colorName = categoryDao.find(company.categoryId).colorType
        viewFavorite = company.favorite

        viewTags = companyDao.findByTag(company.id).take(5)

        jobEvaluationDao.find(id)?.let {
            var score = 0
            if(it.correctSentence) score += 20
            if(it.developmentEnv) score += 10
            if(it.appeal) score += 20
            if(it.wantSkill) score += 20
            if(it.personImage) score += 10
            if(it.jobOfferReason) score += 20
            viewJobEvaluation = score.toString() + JOB_EVALUATION_UNIT
        }
    }

    fun change(vm: CompanyViewModel) {
        viewName = vm.viewName
        viewWantedJob = vm.viewWantedJob
        viewJobEvaluation = vm.viewJobEvaluation
        viewEmployeesNum = vm.viewEmployeesNum
        viewSalary = vm.viewSalary
        colorName = vm.colorName
        viewFavorite = vm.viewFavorite
        viewTags = vm.viewTags
    }

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResNormal(colorName, context)

    override fun equals(other: Any?): Boolean = (other as CompanyViewModel).id == id || super.equals(other)

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