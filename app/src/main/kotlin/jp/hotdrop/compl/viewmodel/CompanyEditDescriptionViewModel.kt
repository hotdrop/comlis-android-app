package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class CompanyEditDescriptionViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao

    lateinit var viewNote: String
    private var companyId: Int = -1

    fun loadData(companyId: Int): Completable {
        return companyDao.findObserve(companyId)
                .flatMapCompletable { company ->
                    setData(company)
                    Completable.complete()
                }
    }

    private fun setData(company: Company) {
        viewNote = company.note ?: ""
        companyId = company.id
    }

    fun update() {
        val company =  makeCompany()
        companyDao.updateDescription(company)
    }

    private fun makeCompany() = Company().apply {
        id = companyId
        note = if(viewNote != "") viewNote else null
    }
}