package jp.hotdrop.compl.dao

import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.model.Category_Relation

object CategoryDao {

    var orma = OrmaHolder.ORMA

    /**
     * 今はGroupNameが重複しない前提としている。あまり良くない・・
     */
    fun find(name: String): Category {
        return categoryRelation().selector().nameEq(name).first()
    }

    fun find(id: Int): Category {
        return categoryRelation().selector().idEq(id).first()
    }

    fun findAll(): MutableList<Category> {
        return categoryRelation().selector()
                .orderByOrderAsc()
                .toList()
    }

    fun insert(argName: String, argColorType: String) {
        val category = Category().apply {
            name = argName
            colorType = argColorType
            order = maxGroupOrder() + 1
        }
        categoryRelation().inserter().execute(category)
    }

    fun update(category: Category) {
        categoryRelation().updater()
                .name(category.name)
                .colorType(category.colorType)
                .idEq(category.id)
                .execute()
    }

    fun exist(name: String): Boolean {
        return !categoryRelation().selector().nameEq(name).isEmpty
    }

    fun exist(name:String, id: Int): Boolean {
        return !categoryRelation().selector().nameEq(name).idNotEq(id).isEmpty
    }

    private fun categoryRelation(): Category_Relation {
        return orma.relationOfCategory()
    }

    private fun maxGroupOrder(): Int {
        return categoryRelation().selector().maxByOrder() ?: 0
    }

}