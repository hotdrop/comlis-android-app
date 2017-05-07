package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil
import javax.inject.Inject

class CompanyEditInfoViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    lateinit var viewEmployeesNum: String
    lateinit var viewSalaryLow: String
    lateinit var viewSalaryHigh: String
    lateinit var viewWantedJob: String
    lateinit var viewWorkPlace: String
    lateinit var viewUrl: String
    lateinit var colorName: String

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
        colorName = categoryDao.find(company.categoryId).colorType
        companyId = company.id
    }

    fun isAllNumbers(value: String): Boolean {
        // 未入力（空）ならOK。ブランクが入っていたらダメなのでisBlankではなくisEmptyにしている。
        if(value.isEmpty()) return true
        return value.isNumber()
    }

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResDark(colorName, context)
    }

    fun update() {
        val company =  makeCompany()
        companyDao.updateInformation(company)
    }

    private fun makeCompany() = Company().apply {
        id = companyId
        employeesNum = viewEmployeesNum.toIntOrZero()
        salaryLow = viewSalaryLow.toIntOrZero()
        salaryHigh = viewSalaryHigh.toIntOrZero()
        wantedJob = viewWantedJob.toStringOrNull()
        workPlace = viewWorkPlace .toStringOrNull()
        url = viewUrl.toStringOrNull()
    }
}