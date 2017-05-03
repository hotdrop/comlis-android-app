package jp.hotdrop.compl.viewmodel

import android.content.Context
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class CompanyRegisterViewModel @Inject constructor(val context: Context,
                                                   val companyDao: CompanyDao,
                                                   val categoryDao: CategoryDao): ViewModel() {

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
    // viewOrder is not declared. Because autoSet max+1 value when insert in CompanyDao

    fun existName(name: String): Boolean {
        if(name.trim() == "") return false
        return companyDao.exist(name)
    }

    fun checkNumber(value: String): Boolean {
        if(value.trim() == "") return true
        return value.isNumber()
    }

    fun getCategoryName(selectedCategorySpinnerId: Int): String {
        val category = categoryDao.find(selectedCategorySpinnerId)
        return category.name
    }

    fun register(selectedCategorySpinnerId: Int) {
        val company =  makeData(selectedCategorySpinnerId)
        companyDao.insert(company)
    }

    private fun makeData(selectedCategorySpinnerId: Int) = Company().apply {
        name = viewName
        categoryId = selectedCategorySpinnerId

        overview = if(viewOverview != "") viewOverview else null

        employeesNum = if(viewEmployeesNum != "") viewEmployeesNum.toInt() else 0
        salaryLow = if(viewSalaryLow != "") viewSalaryLow.toInt() else 0
        salaryHigh = if(viewSalaryHigh != "") viewSalaryHigh.toInt() else 0
        wantedJob = if(viewWantedJob != "") viewWantedJob else null
        workPlace = if(viewWorkPlace != "") viewWorkPlace else null
        url = if(viewUrl != "") viewUrl else null

        doingBusiness = if(viewDoingBusiness != "") viewDoingBusiness else null
        wantBusiness = if(viewWantBusiness != "") viewWantBusiness else null

        note = if(viewNote != "") viewNote else null
    }
}