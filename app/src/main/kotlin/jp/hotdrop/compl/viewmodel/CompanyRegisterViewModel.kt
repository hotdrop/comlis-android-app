package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.AppCode

class CompanyRegisterViewModel: ViewModel() {

    var viewName = ""
    var viewOverview = ""
    var viewEmployeesNum = ""
    var viewSalaryLow = ""
    var viewSalaryHigh = ""
    var viewWantedJob = ""
    var viewUrl = ""
    var viewNote = ""
    // orderはinsert時にMAX値を入れる

    fun register(selectedCategorySpinnerId: Int): Int {
        val code = canRegister()
        if(code != AppCode.OK) {
            return code
        }
        val company =  makeData(selectedCategorySpinnerId)
        CompanyDao.insert(company)

        return AppCode.OK
    }

    private fun canRegister(): Int {
        if(viewName.trim() == "") {
            return AppCode.ERROR_EMPTY_COMPANY_NAME
        }
        if(!viewEmployeesNum.isNumber()) {
            return AppCode.ERROR_NOT_NUMBER_EMPLOYEES_NUM
        }
        if(!viewSalaryLow.isNumber()) {
            return AppCode.ERROR_NOT_NUMBER_SALARY
        }
        if(viewSalaryHigh != "" && !viewSalaryHigh.isNumber()) {
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
        wantedJob = if(viewWantedJob != "") viewWantedJob else null
        url = if(viewUrl != "") viewUrl else null
        note = if(viewNote != "") viewNote else null
    }

}