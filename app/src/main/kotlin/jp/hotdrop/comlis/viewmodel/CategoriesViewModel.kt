package jp.hotdrop.comlis.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import jp.hotdrop.comlis.BR
import jp.hotdrop.comlis.model.Category
import jp.hotdrop.comlis.repository.category.CategoryRepository
import jp.hotdrop.comlis.repository.company.CompanyRepository
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(
        private val context: Context,
        private val categoryRepository: CategoryRepository,
        private val companyRepository: CompanyRepository
): ViewModel() {

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun getData(): Single<List<CategoryViewModel>> =
            Single.just(categoryRepository.findAll()
                    .map {
                        val itemCount = getRegisterCompanyCount(it.id)
                        CategoryViewModel(it, itemCount, context)
                    })

    fun existName(categoryName: String) =
            categoryRepository.exist(categoryName)

    fun existNameExclusionId(categoryName: String, id: Int) =
            categoryRepository.existExclusionId(categoryName, id)

    fun getViewModel(name: String): CategoryViewModel {
        val itemCount = categoryRepository.find(name).let { getRegisterCompanyCount(it.id) }
        return CategoryViewModel(categoryRepository.find(name), itemCount, context)
    }

    fun register(argName: String, argColorType: String) {
        categoryRepository.insert(Category().apply {
            name = argName
            colorType = argColorType
        })
    }

    fun update(vm: CategoryViewModel, newName: String, newColorType: String) {
        categoryRepository.update(vm.category.apply {
            name = newName
            colorType = newColorType
        })
    }

    fun updateItemOrder(categoryIds: List<Int>) {
        categoryRepository.updateAllOrder(categoryIds)
    }

    fun delete(vm: CategoryViewModel) {
        categoryRepository.delete(vm.category)
    }

    fun visibilityEmptyMessageOnScreen() {
        emptyMessageVisibility = View.VISIBLE
    }

    fun goneEmptyMessageOnScreen() {
        emptyMessageVisibility = View.GONE
    }

    private fun getRegisterCompanyCount(categoryId: Int) =
            companyRepository.countByCategory(categoryId)
}