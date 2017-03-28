package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.AppCode
import jp.hotdrop.compl.util.DataChecker

class CompanyEditViewModel(val companyId: Int) {

    val company: Company = CompanyDao.find(companyId)

    var viewName: String
    var viewOverview: String
    var viewEmployeesNum: String
    var viewSalaryLow: String
    var viewSalaryHigh: String
    var viewWantedJob: String
    var viewUrl: String
    var viewNote: String

    var categoryName: String

    init {
        viewName = company.name
        viewOverview = company.overview ?: ""

        viewEmployeesNum = if(company.employeesNum > 0) company.employeesNum.toString() else ""
        viewSalaryLow = if(company.salaryLow > 0) company.salaryLow.toString() else ""
        viewSalaryHigh = if(company.salaryHigh > 0) company.salaryHigh.toString() else ""
        viewWantedJob = company.wantedJob ?: ""
        viewUrl = company.url ?: ""
        viewNote = company.note ?: ""

        categoryName = CategoryDao.find(company.categoryId).name
    }

    fun update(selectedCategorySpinnerId: Int): Int {
        val code = canUpdate()
        if(code != AppCode.OK) {
            return code
        }
        val company =  makeData(selectedCategorySpinnerId)
        CompanyDao.update(company)

        return AppCode.OK
    }

    private fun canUpdate(): Int {
        if(viewName.trim() == "") {
            return AppCode.ERROR_EMPTY_COMPANY_NAME
        }
        if(!DataChecker.isNumber(viewEmployeesNum)) {
            return AppCode.ERROR_NOT_NUMBER_EMPLOYEES_NUM
        }
        if(!DataChecker.isNumber(viewSalaryLow)) {
            return AppCode.ERROR_NOT_NUMBER_SALARY
        }
        if(viewSalaryHigh != "" && !DataChecker.isNumber(viewSalaryHigh)) {
            return AppCode.ERROR_NOT_NUMBER_SALARY
        }
        return AppCode.OK
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
        url = if(viewUrl != "") viewUrl else null
        note = if(viewNote != "") viewNote else null

        viewOrder = if(categoryId != company.categoryId) CompanyDao.maxOrder() + 1 else company.viewOrder
        favorite = company.favorite
    }
}