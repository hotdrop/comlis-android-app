package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.AppCode
import jp.hotdrop.compl.util.DataChecker

class CompanyRegisterViewModel {

    var name = ""
    var overview = ""
    var employeesNum = ""
    var salaryLow = ""
    var salaryHigh = ""
    var url = ""


    fun register(selectedCategorySpinnerId: Int): Int {
        val code = canRegister()
        if(code != AppCode.OK) {
            return code
        }
        val makeCompany =  makeData(selectedCategorySpinnerId)
        CompanyDao.insert(makeCompany)

        return AppCode.OK
    }

    private fun canRegister(): Int {
        if(name.trim() == "") {
            return AppCode.ERROR_EMPTY_COMPANY_NAME
        }
        if(!DataChecker.isNumber(employeesNum)) {
            return AppCode.ERROR_NOT_NUMBER_EMPLOYEES_NUM
        }
        if(!DataChecker.isNumber(salaryLow)) {
            return AppCode.ERROR_NOT_NUMBER_SALARY
        }
        if(salaryHigh != "" && !DataChecker.isNumber(salaryHigh)) {
            return AppCode.ERROR_NOT_NUMBER_SALARY
        }
        return AppCode.OK
    }

    private fun makeData(selectedCategorySpinnerId: Int) = Company().apply {
        name = this@CompanyRegisterViewModel.name
        categoryId = selectedCategorySpinnerId
        overview = if(this@CompanyRegisterViewModel.overview != "") this@CompanyRegisterViewModel.overview else null
        employeesNum = if(this@CompanyRegisterViewModel.employeesNum != "") this@CompanyRegisterViewModel.employeesNum.toInt() else 0
        salaryLow = if(this@CompanyRegisterViewModel.salaryLow != "") this@CompanyRegisterViewModel.salaryLow.toInt() else 0
        salaryHigh = if(this@CompanyRegisterViewModel.salaryHigh != "") this@CompanyRegisterViewModel.salaryHigh.toInt() else 0
        url = if(this@CompanyRegisterViewModel.url != "") this@CompanyRegisterViewModel.url else null
    }

}