package jp.hotdrop.compl.dao

import io.reactivex.Single
import jp.hotdrop.compl.model.Category
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryDao @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma

    fun find(id: Int) =
            categoryRelation().selector()
                    .idEq(id)
                    .value()

    fun find(name: String) =
            categoryRelation().selector()
                    .nameEq(name)
                    .value()

    fun findAll(): Single<List<Category>> =
         categoryRelation().selector()
                 .orderByViewOrderAsc()
                 .executeAsObservable()
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

    fun exist(name: String): Boolean {
        return !categoryRelation().selector()
                .nameEq(name)
                .isEmpty
    }

    fun existExclusionId(name: String, id: Int): Boolean {
        return !categoryRelation().selector()
                .nameEq(name)
                .idNotEq(id)
                .isEmpty
    }

    private fun maxOrder() = categoryRelation().selector().maxByViewOrder() ?: 0

    private fun categoryRelation() = orma.relationOfCategory()
}