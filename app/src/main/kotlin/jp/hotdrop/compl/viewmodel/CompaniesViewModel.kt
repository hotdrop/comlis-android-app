package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject

class CompaniesViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var jobEvaluationDao: JobEvaluationDao

    private lateinit var viewModels: List<CompanyViewModel>

    fun loadData(categoryId: Int): Completable {
        return companyDao.findByCategory(categoryId)
                .flatMapCompletable { companies ->
                    setData(companies)
                    Completable.complete()
                }
    }

    private fun setData(companies: List<Company>) {
        viewModels = companies.map {
            CompanyViewModel(it, context, companyDao, categoryDao, jobEvaluationDao)
        }
    }

    fun isNotEmpty(): Boolean {
        return viewModels.isNotEmpty()
    }

    fun getCompanyViewModels(): List<CompanyViewModel> {
        return viewModels
    }

    fun getCompanyViewModel(companyId: Int): CompanyViewModel {
        return CompanyViewModel(companyDao.find(companyId), context, companyDao, categoryDao, jobEvaluationDao)
    }

    fun updateItemOrder(companyIds: List<Int>) {
        companyDao.updateAllOrder(companyIds)
    }

    fun getTagAssociateViewModel(tag: Tag): TagAssociateViewModel {
        return TagAssociateViewModel(tag = tag, context = context, companyDao = companyDao)
    }

}