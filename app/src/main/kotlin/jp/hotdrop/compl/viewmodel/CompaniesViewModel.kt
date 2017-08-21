package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
import javax.inject.Inject

class CompaniesViewModel @Inject constructor(private val context: Context): ViewModel() {

    @Inject
    lateinit var companyRepository: CompanyRepository
    @Inject
    lateinit var categoryRepository: CategoryRepository

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun getData(categoryId: Int): Single<List<CompanyViewModel>> =
            companyRepository.findByCategory(categoryId)
                    .map { companies ->
                        companies.map {
                            CompanyViewModel(it, context, companyRepository, categoryRepository)
                        }
                    }

    fun getCompanyViewModel(companyId: Int) =
            companyRepository.find(companyId).let {
                CompanyViewModel(it, context, companyRepository, categoryRepository)
            }

    fun updateItemOrder(companyIds: List<Int>) {
        companyRepository.updateAllOrder(companyIds)
    }

    // 関連付けしているタグしか取得していないため、無条件で第二引数をtrueにする。（関連付けられているという意味）
    fun getTagAssociateViewModel(tag: Tag) =
            TagAssociateViewModel(tag, true, context)

    fun visibilityEmptyMessageOnScreen() {
        emptyMessageVisibility = View.VISIBLE
    }

    fun goneEmptyMessageOnScreen() {
        emptyMessageVisibility = View.GONE
    }
}