package jp.hotdrop.compl.repository.category

import jp.hotdrop.compl.model.Category
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
        private val localDataSource: CategoryLocalDataSource
) {

    fun find(id: Int) =
            localDataSource.find(id)

    fun find(name: String) =
            localDataSource.find(name)

    fun findAll(): List<Category> =
            localDataSource.findAll()

    fun insert(category: Category) {
        localDataSource.insert(category)
    }

    fun update(category: Category) {
        localDataSource.update(category)
    }

    fun updateAllOrder(categoryIds: List<Int>) {
        localDataSource.updateAllOrder(categoryIds)
    }

    fun delete(category: Category) {
        localDataSource.delete(category)
    }

    fun exist(name: String) =
            localDataSource.exist(name)

    fun existExclusionId(name: String, id: Int) =
            localDataSource.existExclusionId(name, id)
}