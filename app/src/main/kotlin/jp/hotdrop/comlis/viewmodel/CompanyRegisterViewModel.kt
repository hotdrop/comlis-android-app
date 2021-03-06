package jp.hotdrop.comlis.viewmodel

import jp.hotdrop.comlis.model.Company
import jp.hotdrop.comlis.repository.category.CategoryLocalDataSource
import jp.hotdrop.comlis.repository.company.CompanyRepository
import javax.inject.Inject

class CompanyRegisterViewModel @Inject constructor(
        private val companyDao: CompanyRepository,
        private val categoryDao: CategoryLocalDataSource
): ViewModel() {

    var viewName = ""
    var viewOverview = ""
    var viewEmployeesNum = ""
    var viewSalaryLow = ""
    var viewSalaryHigh = ""
    var viewWantedJob = ""
    var viewWorkPlace = ""
    var viewUrl = ""
    var viewDoingBusiness = ""
    var viewWantBusiness = ""
    var viewNote = ""
    // viewOrder is not declared. Because autoSet max+1 value when insert in CompanyRepository

    fun existName(name: String) =
            companyDao.exist(name)

    fun getCategoryName(selectedCategorySpinnerId: Int) =
            categoryDao.find(selectedCategorySpinnerId).name

    fun register(selectedCategorySpinnerId: Int) {
        val company =  makeData(selectedCategorySpinnerId)
        companyDao.insert(company)
    }

    private fun makeData(selectedCategorySpinnerId: Int) = Company().apply {
        name = viewName
        categoryId = selectedCategorySpinnerId
        overview = viewOverview.toStringOrNull()
        employeesNum = viewEmployeesNum.toIntOrZero()
        salaryLow = viewSalaryLow.toIntOrZero()
        salaryHigh = viewSalaryHigh.toIntOrZero()
        wantedJob = viewWantedJob.toStringOrNull()
        workPlace = viewWorkPlace.toStringOrNull()
        url = viewUrl.toStringOrNull()
        doingBusiness = viewDoingBusiness.toStringOrNull()
        wantBusiness = viewWantBusiness.toStringOrNull()
        note = viewNote.toStringOrNull()
    }
}