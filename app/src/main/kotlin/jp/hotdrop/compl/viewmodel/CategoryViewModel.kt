package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.util.ColorUtil

class CategoryViewModel(private var category: Category,
                        private val context: Context,
                        private val categoryDao: CategoryDao,
                        companyDao: CompanyDao): ViewModel() {

    var viewName = category.name
    // バインドしているので文字列にしないとダメ
    var itemCount = companyDao.countByCategory(category.id).toString()

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResLight(category.colorType, context)

    fun getId() = category.id
    fun getColorType() = category.colorType

    fun change(vm: CategoryViewModel) {
        category = vm.category
        viewName = vm.viewName
        itemCount = vm.itemCount
    }

    override fun equals(other: Any?): Boolean =
            (other as CategoryViewModel).category.id == category.id || super.equals(other)

    fun update(newCategoryName: String, newColorType: String) {
        viewName = newCategoryName
        category.name = newCategoryName
        category.colorType = newColorType
        categoryDao.update(category)
    }

    fun delete() {
        categoryDao.delete(category)
    }

    fun isRegisterCompanyInCategory(): Boolean {
        return itemCount.toInt() > 0
    }
}