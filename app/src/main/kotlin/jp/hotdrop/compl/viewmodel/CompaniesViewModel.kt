package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject

class CompaniesViewModel @Inject constructor(private val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var jobEvaluationDao: JobEvaluationDao

    lateinit var viewModels: List<CompanyViewModel>

    fun loadData(categoryId: Int): Completable {
        return companyDao.findByCategory(categoryId)
                .flatMapCompletable {
                    setData(it)
                    Completable.complete()
                }
    }

    private fun setData(companies: List<Company>) {
        viewModels = companies.map {
            // TODO Dao渡しまくるのなんとかしたい・・
            CompanyViewModel(it, context, companyDao, categoryDao, jobEvaluationDao)
        }
    }

    fun isNotEmpty(): Boolean {
        return viewModels.isNotEmpty()
    }

    fun getData(): List<CompanyViewModel> {
        return viewModels
    }

    fun getCompanyViewModel(companyId: Int): CompanyViewModel {
        return CompanyViewModel(companyDao.find(companyId), context, companyDao, categoryDao, jobEvaluationDao)
    }

    fun updateItemOrder(companyIds: List<Int>) {
        companyDao.updateAllOrder(companyIds)
    }

    fun getTagAssociateViewModel(tag: Tag): TagAssociateViewModel {
        // 関連付けしているタグしか取得していないため、無条件で第二引数をtrue（関連付けられているという意味）にする。
        return TagAssociateViewModel(tag, true, context)
    }

}