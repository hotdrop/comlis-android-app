package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class CompanyEditOverviewViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao

    lateinit var viewName: String
    lateinit var viewOverview: String
    private var companyId: Int = -1
    var categoryId: Int = -1

    var isChangeCategory: Boolean = false

    fun loadData(companyId: Int): Completable {
        return companyDao.findObserve(companyId)
                .flatMapCompletable { company ->
                    setData(company)
                    Completable.complete()
                }
    }

    private fun setData(company: Company) {
        viewName = company.name
        viewOverview = company.overview ?: ""
        companyId = company.id
        categoryId = company.categoryId
    }

    fun existName(name: String): Boolean {
        if(name.trim() == "") return false
        return companyDao.exist(name, companyId)
    }

    fun update(selectedCategorySpinnerId: Int) {
        isChangeCategory = (categoryId != selectedCategorySpinnerId)
        val company =  makeCompany(selectedCategorySpinnerId)
        companyDao.updateOverview(company)
    }

    private fun makeCompany(selectedCategorySpinnerId: Int) = Company().apply {
        id = companyId
        name = viewName
        categoryId = selectedCategorySpinnerId
        overview = if(viewOverview != "") viewOverview else null
    }
}