package jp.hotdrop.compl.repository.category

import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.repository.OrmaHolder
import java.util.*
import javax.inject.Inject

class CategoryLocalDataSource @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma

    fun find(id: Int) =
            categoryRelation().selector()
                    .idEq(id)
                    .value()

    fun find(name: String) =
            categoryRelation().selector()
                    .nameEq(name)
                    .value()

    // CompanyDaoと同理由によりDescで取得する
    fun findAll(): List<Category> =
            categoryRelation().selector()
                 .orderByViewOrderDesc()
                 .toList()

    fun insert(argName: String, argColorType: String) {
        val category = Category().apply {
            name = argName
            colorType = argColorType
            viewOrder = maxOrder() + 1
            registerDate = Date(System.currentTimeMillis())
        }
        orma.transactionSync {
            categoryRelation().inserter().execute(category)
        }
    }

    fun update(category: Category) {
        orma.transactionSync {
            categoryRelation().updater()
                    .name(category.name)
                    .colorType(category.colorType)
                    .updateDate(Date(System.currentTimeMillis()))
                    .idEq(category.id)
                    .execute()
        }
    }

    fun updateAllOrder(categoryIds: List<Int>) {
        orma.transactionSync {
            categoryIds.forEachIndexed { index, id ->
                categoryRelation().updater()
                        .viewOrder(index)
                        .idEq(id)
                        .execute()
            }
        }
    }

    fun delete(category: Category) {
        orma.transactionSync {
            categoryRelation().deleter()
                    .idEq(category.id)
                    .execute()
        }
    }

    fun exist(name: String) =
            !categoryRelation().selector()
                    .nameEq(name)
                    .isEmpty

    fun existExclusionId(name: String, id: Int) =
            !categoryRelation().selector()
                    .nameEq(name)
                    .idNotEq(id)
                    .isEmpty

    private fun maxOrder() =
            categoryRelation().selector()
                    .maxByViewOrder() ?: 0

    private fun categoryRelation() =
            orma.relationOfCategory()
}