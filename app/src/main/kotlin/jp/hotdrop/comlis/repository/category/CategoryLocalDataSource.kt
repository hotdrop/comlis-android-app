package jp.hotdrop.comlis.repository.category

import jp.hotdrop.comlis.model.Category
import jp.hotdrop.comlis.repository.OrmaHolder
import java.util.*
import javax.inject.Inject

class CategoryLocalDataSource @Inject constructor(
        ormaHolder: OrmaHolder
) {

    private val orma = ormaHolder.orma

    fun find(id: Int) =
            categoryRelation().selector()
                    .idEq(id)
                    .value()

    fun find(name: String) =
            categoryRelation().selector()
                    .nameEq(name)
                    .value()

    fun findAll(): List<Category> =
            categoryRelation().selector()
                    .orderByViewOrderAsc()
                    .toList()

    fun insert(category: Category) {
        orma.transactionSync {
            categoryRelation().inserter()
                    .execute(category.apply {
                        viewOrder = maxOrder() + 1
                        registerDate = Date(System.currentTimeMillis())
                    })
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