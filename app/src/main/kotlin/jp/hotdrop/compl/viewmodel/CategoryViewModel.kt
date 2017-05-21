package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.util.ColorUtil

class CategoryViewModel(var category: Category, val context: Context,
                        val companyDao: CompanyDao): ViewModel() {

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = category.name
    var itemCount: String

    init {
        itemCount = companyDao.countByCategory(category.id).toString()
    }

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResLight(category.colorType, context)

    fun change(vm: CategoryViewModel) {
        category = vm.category
        viewName = vm.viewName
        itemCount = vm.itemCount
    }

    override fun equals(other: Any?): Boolean =
            (other as CategoryViewModel).category.id == category.id || super.equals(other)

    fun makeCategory(): Category = category.apply {
        name = viewName
    }
}