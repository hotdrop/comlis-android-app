package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.R
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

    fun update(selectedCategorySpinnerId: Int): ErrorMessage? {
        val errorMessage = canUpdate()
        if(errorMessage != null) {
            return errorMessage
        }
        isChangeCategory = (categoryId != selectedCategorySpinnerId)
        val company =  makeCompany(selectedCategorySpinnerId)
        companyDao.updateOverview(company)

        return null
    }

    private fun canUpdate(): ErrorMessage? {
        if(viewName.trim() == "") {
            return ErrorMessage(context.getString(R.string.error_message_empty_company_name))
        }
        return null
    }

    private fun makeCompany(selectedCategorySpinnerId: Int) = Company().apply {
        id = companyId
        name = viewName
        categoryId = selectedCategorySpinnerId
        overview = if(viewOverview != "") viewOverview else null
    }
}