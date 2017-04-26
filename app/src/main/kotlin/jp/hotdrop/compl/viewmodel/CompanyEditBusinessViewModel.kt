package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class CompanyEditBusinessViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao

    lateinit var viewDoingBusiness: String
    lateinit var viewWantBusiness: String
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
    }

    fun update() {
        val company =  makeCompany()
        companyDao.updateBusiness(company)
    }

    private fun makeCompany() = Company().apply {
        id = companyId
        doingBusiness = if(viewDoingBusiness != "") viewDoingBusiness else null
        wantBusiness = if(viewWantBusiness != "") viewWantBusiness else null
    }

}