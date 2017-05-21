package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil
import javax.inject.Inject

class CompanyEditBusinessViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    lateinit var viewDoingBusiness: String
    lateinit var viewWantBusiness: String
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
        viewDoingBusiness = company.doingBusiness ?: ""
        viewWantBusiness = company.wantBusiness ?: ""
        companyId = company.id
        colorName = categoryDao.find(company.categoryId).colorType
    }

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResDark(colorName, context)

    fun update() {
        val company =  makeCompany()
        companyDao.updateBusiness(company)
    }

    private fun makeCompany() = Company().apply {
        id = companyId
        doingBusiness = viewDoingBusiness.toStringOrNull()
        wantBusiness = viewWantBusiness.toStringOrNull()
    }
}