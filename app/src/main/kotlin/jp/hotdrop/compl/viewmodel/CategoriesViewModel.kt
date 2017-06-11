package jp.hotdrop.compl.viewmodel

import android.content.Context
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Category
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var companyDao: CompanyDao

    private lateinit var viewModels: List<CategoryViewModel>

    fun loadData(): Completable {
        return categoryDao.findAll()
                .flatMapCompletable {
                    setData(it)
                    Completable.complete()
                }
    }

    private fun setData(categories: List<Category>) {
        viewModels = categories.map {
            CategoryViewModel(it, context, categoryDao, companyDao)
        }
    }

    fun isNotEmpty(): Boolean {
        return viewModels.isNotEmpty()
    }

    fun getData(): List<CategoryViewModel> {
        return viewModels
    }

    fun updateItemOrder(categoryIds: List<Int>) {
        categoryDao.updateAllOrder(categoryIds)
    }

    fun existName(categoryName: String): Boolean {
        return categoryDao.exist(categoryName)
    }

    fun existNameExclusionId(categoryName: String, id: Int): Boolean {
        return categoryDao.existExclusionId(categoryName, id)
    }

    fun register(categoryName: String, colorType: String) {
        categoryDao.insert(categoryName, colorType)
    }

    fun getCategoryViewModel(categoryName: String): CategoryViewModel {
        val category = categoryDao.find(categoryName)
        return CategoryViewModel(category, context, categoryDao, companyDao)
    }
}