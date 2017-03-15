package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.AppCode
import jp.hotdrop.compl.util.DataChecker

class CompanyRegisterViewModel {

    var name = ""
    var salaryLow = "0"
    var salaryHigh = "0"

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
        salaryLow = if(this@CompanyRegisterViewModel.salaryLow != "") this@CompanyRegisterViewModel.salaryLow.toInt() else 0
        salaryHigh = if(this@CompanyRegisterViewModel.salaryHigh != "") this@CompanyRegisterViewModel.salaryHigh.toInt() else 0
    }

}