package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.util.ColorUtil

class CategoryViewModel(var category: Category,
                        private var registerCompanyCount: Int,
                        private val context: Context): ViewModel() {

    var viewName = category.name

    fun isRegisterCompanyInCategory() = registerCompanyCount > 0

    @ColorRes
    fun getColorRes() = ColorUtil.getResLight(category.colorType, context)

    fun getId() = category.id

    fun getColorType() = category.colorType

    // TextViewにバインドしているので文字列にする
    fun getRegisterCompanyCountToString() = registerCompanyCount.toString()

    fun change(vm: CategoryViewModel) {
        category = vm.category
        viewName = vm.viewName
        registerCompanyCount = vm.registerCompanyCount
    }

    override fun equals(other: Any?) =
            (other as CategoryViewModel).category.id == category.id || super.equals(other)

    override fun hashCode(): Int {
        var result = category.hashCode()
        result = 31 * result + category.id.hashCode()
        result = 31 * result + context.hashCode()
        result = 31 * result + viewName.hashCode()
        return result
    }
}