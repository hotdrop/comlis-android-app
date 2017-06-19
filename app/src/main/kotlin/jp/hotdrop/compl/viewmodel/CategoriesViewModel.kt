package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var companyDao: CompanyDao

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun getData(): Single<List<CategoryViewModel>> {
        return categoryDao.findAll()
                .map { categories ->
                    categories.map {
                        val itemCount = getRegisterCompanyCount(it.id)
                        CategoryViewModel(it, itemCount, context)
                    }
                }
    }

    fun existName(categoryName: String): Boolean {
        return categoryDao.exist(categoryName)
    }

    fun existNameExclusionId(categoryName: String, id: Int): Boolean {
        return categoryDao.existExclusionId(categoryName, id)
    }

    fun getViewModel(name: String): CategoryViewModel {
        val category = categoryDao.find(name)
        val itemCount = getRegisterCompanyCount(category.id)
        return CategoryViewModel(categoryDao.find(name), itemCount, context)
    }

    fun register(categoryName: String, colorType: String) {
        categoryDao.insert(categoryName, colorType)
    }

    fun update(vm: CategoryViewModel, newName: String, newColorType: String) {
        val c = vm.category.apply {
            name = newName
            colorType = newColorType
        }
        categoryDao.update(c)
    }

    fun updateItemOrder(categoryIds: List<Int>) {
        categoryDao.updateAllOrder(categoryIds)
    }

    fun delete(vm: CategoryViewModel) {
        categoryDao.delete(vm.category)
    }

    fun visibilityEmptyMessageOnScreen() {
        emptyMessageVisibility = View.VISIBLE
    }

    fun goneEmptyMessageOnScreen() {
        emptyMessageVisibility = View.GONE
    }

    private fun getRegisterCompanyCount(categoryId: Int): Int {
        return companyDao.countByCategory(categoryId)
    }
}