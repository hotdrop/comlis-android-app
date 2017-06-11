package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.databinding.ItemCompanyBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil

class CompanyViewModel(private var company: Company,
                       private val context: Context,
                       private val companyDao: CompanyDao,
                       categoryDao: CategoryDao,
                       jobEvaluationDao: JobEvaluationDao): ViewModel() {

    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)
    private val JOB_EVALUATION_UNIT = context.getString(R.string.label_job_evaluation_unit)

    var viewName = company.name
    var viewWantedJob = company.wantedJob ?: ""
    var viewJobEvaluation = "0" + JOB_EVALUATION_UNIT
    var viewEmployeesNum = company.employeesNum.toString() + EMPLOYEES_NUM_UNIT
    var viewSalary = company.salaryLow.toString() + SALARY_UNIT
    var viewFavorite = company.favorite

    var colorName = categoryDao.find(company.categoryId).colorType
    var viewTags = companyDao.findByTag(company.id).take(5)


    init {
        if(company.salaryHigh > 0) {
            viewSalary += SALARY_RANGE_MARK + company.salaryHigh.toString() + SALARY_UNIT
        }

        jobEvaluationDao.find(company.id)?.let {
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

    fun getId(): Int {
        return company.id
    }

    fun change(vm: CompanyViewModel) {
        company = vm.company
        viewName = vm.viewName
        viewWantedJob = vm.viewWantedJob
        viewJobEvaluation = vm.viewJobEvaluation
        viewEmployeesNum = vm.viewEmployeesNum
        viewSalary = vm.viewSalary
        viewFavorite = vm.viewFavorite
        colorName = vm.colorName
        viewTags = vm.viewTags
    }

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResNormal(colorName, context)

    override fun equals(other: Any?): Boolean = (other as CompanyViewModel).company.id == company.id || super.equals(other)

    fun onClickFirstFavorite(binding: ItemCompanyBinding) {
        if(viewFavorite == 1) {
            resetFavorite(binding)
        } else {
            binding.animationView1.playAnimation()
            binding.animationView2.reset()
            binding.animationView3.reset()
            viewFavorite = 1
            companyDao.updateFavorite(company.id, viewFavorite)
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
            companyDao.updateFavorite(company.id, viewFavorite)
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
            companyDao.updateFavorite(company.id, viewFavorite)
        }
    }

    fun resetFavorite(binding: ItemCompanyBinding) {
        binding.animationView1.reset()
        binding.animationView2.reset()
        binding.animationView3.reset()
        viewFavorite = 0
        companyDao.updateFavorite(company.id, 0)
    }
}