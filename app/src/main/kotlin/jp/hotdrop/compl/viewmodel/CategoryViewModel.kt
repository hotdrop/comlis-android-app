package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.util.ColorUtil

class CategoryViewModel(var category: Category,
                        var registerCompanyCount: Int,
                        private val context: Context): ViewModel() {

    var viewName = category.name

    override fun equals(other: Any?) =
            (other as CategoryViewModel).category.id == category.id || super.equals(other)

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
}