package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Category

class CategoryViewModel(val category: Category) {

    var viewId = category.id
    var viewName = category.name
    var viewColorType = category.colorType
    var viewPoint = category.point.toString()
    var viewOrder = category.order
    var itemCount = "0"

    init {
        itemCount = CompanyDao.countByCategory(viewId).toString()
    }

    fun change(vm: CategoryViewModel) {
        viewId = vm.viewId
        viewName = vm.viewName
        viewColorType = vm.viewColorType
        viewPoint = vm.viewPoint
        viewOrder = vm.viewOrder
        itemCount = vm.itemCount
    }

    override fun equals(other: Any?): Boolean {
        return (other as CategoryViewModel).viewId == viewId || super.equals(other)
    }

    fun makeCategory(): Category = Category().apply {
        id = viewId
        name = viewName
        colorType = viewColorType
        point = if(viewPoint != "") viewPoint.toInt() else 0
        order = viewOrder
    }
}