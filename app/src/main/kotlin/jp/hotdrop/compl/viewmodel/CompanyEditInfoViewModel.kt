package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class CompanyEditInfoViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao

    lateinit var viewEmployeesNum: String
    lateinit var viewSalaryLow: String
    lateinit var viewSalaryHigh: String
    lateinit var viewWantedJob: String
    lateinit var viewWorkPlace: String
    lateinit var viewUrl: String

    private var companyId: Int = -1

    fun loadData(companyId: Int): Completable {
        return companyDao.findObserve(companyId)
                .flatMapCompletable { company ->
                    setData(company)
                    Completable.complete()
                }
    }

    private fun setData(company: Company) {
        viewEmployeesNum = if(company.employeesNum > 0) company.employeesNum.toString() else ""
        viewSalaryLow = if(company.salaryLow > 0) company.salaryLow.toString() else ""
        viewSalaryHigh = if(company.salaryHigh > 0) company.salaryHigh.toString() else ""
        viewWantedJob = company.wantedJob ?: ""
        viewWorkPlace = company.workPlace ?: ""
        viewUrl = company.url ?: ""

        companyId = company.id
    }

    fun update(): ErrorMessage? {
        val errorMessage = canUpdate()
        if(errorMessage != null) {
            return errorMessage
        }
        val company =  makeCompany()
        companyDao.updateInformation(company)
        return null
    }

    private fun canUpdate(): ErrorMessage? {
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

    private fun makeCompany() = Company().apply {
        id = companyId
        employeesNum = if(viewEmployeesNum != "") viewEmployeesNum.toInt() else 0
        salaryLow = if(viewSalaryLow != "") viewSalaryLow.toInt() else 0
        salaryHigh = if(viewSalaryHigh != "") viewSalaryHigh.toInt() else 0
        wantedJob = if(viewWantedJob != "") viewWantedJob else null
        workPlace = if(viewWorkPlace != "") viewWorkPlace else null
        url = if(viewUrl != "") viewUrl else null
    }
}