package jp.hotdrop.compl.viewmodel

import android.content.Context
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class CompanyEditViewModel(val companyId: Int, val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    val company: Company = companyDao.find(companyId)

    var viewName: String
    var viewOverview: String
    var viewEmployeesNum: String
    var viewSalaryLow: String
    var viewSalaryHigh: String
    var viewWantedJob: String
    var viewWorkPlace = ""
    var viewUrl = ""
    var viewDoingBusiness = ""
    var viewWantBusiness = ""

    var viewNote: String

    var categoryName: String

    init {
        viewName = company.name
        viewOverview = company.overview ?: ""

        viewEmployeesNum = if(company.employeesNum > 0) company.employeesNum.toString() else ""
        viewSalaryLow = if(company.salaryLow > 0) company.salaryLow.toString() else ""
        viewSalaryHigh = if(company.salaryHigh > 0) company.salaryHigh.toString() else ""
        viewWantedJob = company.wantedJob ?: ""
        viewWorkPlace = company.workPlace ?: ""
        viewDoingBusiness = company.doingBusiness ?: ""
        viewWantBusiness = company.wantBusiness ?: ""
        viewUrl = company.url ?: ""
        viewNote = company.note ?: ""

        categoryName = categoryDao.find(company.categoryId).name
    }

    fun update(selectedCategorySpinnerId: Int): ErrorMessage? {
        val errorMessage = canUpdate()
        if(errorMessage != null) {
            return errorMessage
        }
        val company =  makeData(selectedCategorySpinnerId)
        companyDao.update(company)

        return null
    }

    private fun canUpdate(): ErrorMessage? {
        if(viewName.trim() == "") {
            return ErrorMessage(context.getString(R.string.error_message_empty_company_name))
        }
        if(!viewEmployeesNum.isNumber()) {
            return ErrorMessage(context.getString(R.string.error_message_employees_num_not_number))
        }
        if(!viewSalaryLow.isNumber()) {
            return ErrorMessage(context.getString(R.string.error_message_salary_not_number))
        }
        if(viewSalaryHigh != "" && !viewSalaryHigh.isNumber()) {
            return ErrorMessage(context.getString(R.string.error_message_salary_not_number))
        }
        return null
    }

    private fun makeData(selectedCategorySpinnerId: Int) = Company().apply {
        id = companyId
        name = viewName
        categoryId = selectedCategorySpinnerId
        overview = if(viewOverview != "") viewOverview else null
        employeesNum = if(viewEmployeesNum != "") viewEmployeesNum.toInt() else 0
        salaryLow = if(viewSalaryLow != "") viewSalaryLow.toInt() else 0
        salaryHigh = if(viewSalaryHigh != "") viewSalaryHigh.toInt() else 0
        wantedJob = if(viewWantedJob != "") viewWantedJob else null
        workPlace = if(viewWorkPlace != "") viewWorkPlace else null
        url = if(viewUrl != "") viewUrl else null
        note = if(viewNote != "") viewNote else null

        doingBusiness = if(viewDoingBusiness != "") viewDoingBusiness else null
        wantBusiness = if(viewWantBusiness != "") viewWantBusiness else null

        viewOrder = if(categoryId != company.categoryId) companyDao.maxOrder() + 1 else company.viewOrder
        favorite = company.favorite

        registerDate = company.registerDate
        updateDate = company.updateDate
    }
}