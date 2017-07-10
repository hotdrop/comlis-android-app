package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject

class CompaniesViewModel @Inject constructor(private val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var jobEvaluationDao: JobEvaluationDao

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun getData(categoryId: Int): Single<List<CompanyViewModel>> =
        companyDao.findByCategory(categoryId)
                .map { companies ->
                    companies.map {
                        CompanyViewModel(it, context, companyDao, categoryDao, jobEvaluationDao)
                    }
                }

    fun getCompanyViewModel(companyId: Int): CompanyViewModel {
        val company = companyDao.findNonObservable(companyId)
        return CompanyViewModel(company, context, companyDao, categoryDao, jobEvaluationDao)
    }

    fun updateItemOrder(companyIds: List<Int>) {
        companyDao.updateAllOrder(companyIds)
    }

    // 関連付けしているタグしか取得していないため、無条件で第二引数をtrueにする。（関連付けられているという意味）
    fun getTagAssociateViewModel(tag: Tag) = TagAssociateViewModel(tag, true, context)

    fun visibilityEmptyMessageOnScreen() {
        emptyMessageVisibility = View.VISIBLE
    }

    fun goneEmptyMessageOnScreen() {
        emptyMessageVisibility = View.GONE
    }
}