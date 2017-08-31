package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
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
            categoryRepository.findAll()
                    .map {
                        val itemCount = getRegisterCompanyCount(it.id)
                        CategoryViewModel(it, itemCount, context)
                    }
                    .toSingle()

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