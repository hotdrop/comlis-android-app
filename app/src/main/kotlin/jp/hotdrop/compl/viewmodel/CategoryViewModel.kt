package jp.hotdrop.compl.viewmodel

import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Category

class CategoryViewModel(val category: Category) {

    var id = category.id
    var name = category.name
    var point = category.point.toString()
    var viewOrder = category.viewOrder
    var itemCount = "0"

    init {
        itemCount = CompanyDao.countByCategory(id).toString()
    }

    fun change(vm: CategoryViewModel) {
        id = vm.id
        name = vm.name
        point = vm.point
        viewOrder = vm.viewOrder
        itemCount = vm.itemCount
    }

    override fun equals(other: Any?): Boolean {
        return (other as CategoryViewModel).id == id || super.equals(other)
    }
}