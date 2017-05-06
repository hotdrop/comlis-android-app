package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil

class ItemSearchResultViewModel(company: Company, val context: Context, val categoryDao: CategoryDao): ViewModel() {

    var id = company.id
    var categoryId = company.categoryId
    var viewCompanyName = company.name
    var favorite = company.favorite

    lateinit var viewCategoryName: String
    lateinit var colorName: String

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResDark(colorName, context)
    }

    fun loadData() {
        val category = categoryDao.find(categoryId)
        viewCategoryName = category.name
        colorName = category.colorType
    }

}