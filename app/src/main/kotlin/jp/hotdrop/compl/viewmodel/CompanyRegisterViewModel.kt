package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.AppCode
import jp.hotdrop.compl.util.DataChecker

class CompanyRegisterViewModel {

    var viewName = ""
    var viewOverview = ""
    var viewEmployeesNum = ""
    var viewSalaryLow = ""
    var viewSalaryHigh = ""
    var viewUrl = ""
    var viewNote = ""


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
        name = viewName
        categoryId = selectedCategorySpinnerId
        overview = if(viewOverview != "") viewOverview else null
        employeesNum = if(viewEmployeesNum != "") viewEmployeesNum.toInt() else 0
        salaryLow = if(viewSalaryLow != "") viewSalaryLow.toInt() else 0
        salaryHigh = if(viewSalaryHigh != "") viewSalaryHigh.toInt() else 0
        url = if(viewUrl != "") viewUrl else null
        note = if(viewNote != "") viewNote else null
    }

}