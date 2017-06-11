package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.util.ColorUtil

class CategoryViewModel(var category: Category,
                        registerCompanyItemCount: Int,
                        private val context: Context): ViewModel() {

    var viewName = category.name
    // TextViewにバインドしているので文字列にする
    var itemCount = registerCompanyItemCount.toString()

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResLight(category.colorType, context)
    fun getId() = category.id
    fun getColorType() = category.colorType

    override fun equals(other: Any?): Boolean =
            (other as CategoryViewModel).category.id == category.id || super.equals(other)

    fun isRegisterCompanyInCategory(): Boolean {
        return itemCount.toInt() > 0
    }
}