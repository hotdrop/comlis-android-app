package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var categoryRepository: CategoryRepository
    @Inject
    lateinit var companyRepository: CompanyRepository

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
        val itemCount = categoryRepository.find(name).let { category ->
            getRegisterCompanyCount(category.id)
        }
        return CategoryViewModel(categoryRepository.find(name), itemCount, context)
    }

    fun register(categoryName: String, colorType: String) {
        categoryRepository.insert(categoryName, colorType)
    }

    fun update(vm: CategoryViewModel, newName: String, newColorType: String) {
        val c = vm.category.apply {
            name = newName
            colorType = newColorType
        }
        categoryRepository.update(c)
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