package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.util.ColorDataUtil

class CategoryViewModel(var category: Category, val context: Context) {

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = category.name
    var itemCount = "0"

    init {
        itemCount = CompanyDao.countByCategory(category.id).toString()
    }

    @ColorRes
    fun getColorRes(): Int {
        return ContextCompat.getColor(context, ColorDataUtil.getColorLight(category.colorType))
    }

    fun change(vm: CategoryViewModel) {
        this.category = vm.category
        viewName = vm.viewName
        itemCount = vm.itemCount
    }

    override fun equals(other: Any?): Boolean {
        return (other as CategoryViewModel).category.id == category.id || super.equals(other)
    }

    fun makeCategory(): Category = category.apply {
        name = viewName
        itemCount = itemCount
    }
}