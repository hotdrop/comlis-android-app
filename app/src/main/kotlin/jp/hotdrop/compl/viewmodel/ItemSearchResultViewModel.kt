package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil

class ItemSearchResultViewModel(company: Company, val context: Context, val categoryDao: CategoryDao): ViewModel() {

    val id = company.id
    val categoryId = company.categoryId
    val viewCompanyName = company.name
    val favorite = company.favorite

    val viewCategoryName: String
    val colorName: String

    init {
        val category = categoryDao.find(categoryId)
        viewCategoryName = category.name
        colorName = category.colorType
    }

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResDark(colorName, context)
}