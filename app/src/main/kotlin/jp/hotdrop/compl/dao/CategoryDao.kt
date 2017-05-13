package jp.hotdrop.compl.dao

import io.reactivex.Single
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.model.Category_Relation
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryDao @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma

    fun find(id: Int): Category {
        return categoryRelation().selector()
                .idEq(id)
                .value()
    }

    fun find(name: String): Category {
        return categoryRelation().selector()
                .nameEq(name)
                .value()
    }

    fun findAll(): Single<List<Category>> {
        return categoryRelation().selector()
                .orderByViewOrderAsc()
                .executeAsObservable()
                .toList()
    }

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

    fun updateAllOrder(categories: List<Category>) {
        orma.transactionSync {
            for((index, category) in categories.withIndex()) {
                categoryRelation().updater()
                        .viewOrder(index)
                        .idEq(category.id)
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

    fun exist(name: String, id: Int = -1): Boolean {
        if(id == -1) {
            return !categoryRelation().selector()
                    .nameEq(name)
                    .isEmpty
        } else {
            return !categoryRelation().selector()
                    .nameEq(name)
                    .idNotEq(id)
                    .isEmpty
        }
    }

    private fun maxOrder(): Int {
        return categoryRelation().selector().maxByViewOrder() ?: 0
    }

    private fun categoryRelation(): Category_Relation {
        return orma.relationOfCategory()
    }

}