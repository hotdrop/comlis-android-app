package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.R
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
import jp.hotdrop.compl.util.ColorUtil
import jp.hotdrop.compl.view.parts.FavoriteStars

class CompanyViewModel(private var company: Company,
                       private val context: Context,
                       private val companyRepository: CompanyRepository,
                       private val categoryRepository: CategoryRepository): ViewModel() {

    private val JOB_EVALUATION_UNIT = context.getString(R.string.label_job_evaluation_unit)
    private val EMPLOYEES_NUM_UNIT = context.getString(R.string.label_employees_num_unit)
    private val SALARY_UNIT = context.getString(R.string.label_salary_unit)
    private val SALARY_RANGE_MARK = context.getString(R.string.label_salary_range_mark)

    lateinit var viewName: String
    lateinit var viewWantedJob: String
    lateinit var viewJobEvaluation: String
    lateinit var viewEmployeesNum: String
    lateinit var viewSalary: String

    private var viewFavorite = 0

    lateinit var colorName: String
    lateinit var viewTags: List<Tag>
    lateinit var favorites: FavoriteStars

    init {
        setData(company)
    }

    private fun setData(company: Company) {

        viewName = company.name
        viewWantedJob = company.wantedJob ?: ""
        viewJobEvaluation = "0" + JOB_EVALUATION_UNIT
        viewEmployeesNum = if(company.employeesNum > 0) company.employeesNum.toString() + EMPLOYEES_NUM_UNIT else ""
        viewFavorite = company.favorite

        colorName = categoryRepository.find(company.categoryId).colorType
        viewTags = companyRepository.findByTag(company.id).take(5)

        viewSalary = makeViewSalary(company.salaryLow, company.salaryHigh)

        companyRepository.findJobEvaluation(company.id)?.run {
            var score = 0
            if(correctSentence) score += 20
            if(developmentEnv) score += 10
            if(appeal) score += 20
            if(wantSkill) score += 20
            if(personImage) score += 10
            if(jobOfferReason) score += 20
            viewJobEvaluation = score.toString() + JOB_EVALUATION_UNIT
        }
    }

    private fun makeViewSalary(salaryLow: Int, salaryHigh: Int): String {
        if(salaryLow <= 0) {
            return ""
        }

        val viewSalaryLow = salaryLow.toString() + SALARY_UNIT
        if(salaryHigh > 0) {
            val viewSalaryHigh = salaryHigh.toString() + SALARY_UNIT
            return viewSalaryLow + SALARY_RANGE_MARK + viewSalaryHigh
        }

        return viewSalaryLow
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
    fun getColorRes() = ColorUtil.getResNormal(colorName, context)


    fun getId() = company.id

    fun onClickFirstFavorite() {
        if(viewFavorite == 1) {
            resetFavorite()
        } else {
            favorites.playAnimationToOne()
            updateFavorite(1)
        }
    }

    fun onClickSecondFavorite() {
        if(viewFavorite == 2) {
            resetFavorite()
        } else {
            favorites.playAnimationToTwo()
            updateFavorite(2)
        }
    }

    fun onClickThirdFavorite() {
        if(viewFavorite == 3) {
            resetFavorite()
        } else {
            favorites.playAnimationToThree()
            updateFavorite(3)
        }
    }

    private fun resetFavorite() {
        favorites.clear()
        updateFavorite(0)
    }

    fun playFavorite() {
        favorites.playAnimation(viewFavorite)
    }

    private fun updateFavorite(favoriteNum: Int) {
        viewFavorite = favoriteNum
        companyRepository.updateFavorite(company.id, favoriteNum)
    }

    override fun equals(other: Any?) =
            (other as CompanyViewModel).company.id == company.id || super.equals(other)

    override fun hashCode(): Int {
        var result = company.hashCode()
        result = 31 * result + company.id.hashCode()
        result = 31 * result + viewName.hashCode()
        result = 31 * result + viewWantedJob.hashCode()
        result = 31 * result + viewJobEvaluation.hashCode()
        result = 31 * result + viewEmployeesNum.hashCode()
        result = 31 * result + viewSalary.hashCode()
        result = 31 * result + viewFavorite
        result = 31 * result + colorName.hashCode()
        result = 31 * result + viewTags.hashCode()
        result = 31 * result + favorites.hashCode()
        return result
    }
}