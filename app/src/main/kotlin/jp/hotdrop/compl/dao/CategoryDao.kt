package jp.hotdrop.compl.dao

import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.model.Category_Relation

object CategoryDao {

    var orma = OrmaHolder.ORMA

    /**
     * 今はGroupNameが重複しない前提としている。あまり良くない・・
     */
    fun find(name: String): Category {
        return groupRelation().selector().nameEq(name).first()
    }

    fun find(id: Int): Category {
        // TODO ここは保持したコレクションから取得するよう修正する。上も
        return groupRelation().selector().idEq(id).first()
    }

    fun findAll(): MutableList<Category> {
        // TODO DBから取得したぜんGroup情報を保持していくように修正する
        return groupRelation().selector()
                .orderByViewOrderAsc()
                .toList()
    }

    fun insert(argName: String) {
        val group = Category().apply {
            name = argName
            viewOrder = maxGroupOrder() + 1
        }
        groupRelation().inserter().execute(group)
    }

    fun update(group: Category) {
        groupRelation().updater().name(group.name).execute()
    }

    fun exist(name: String): Boolean {
        return !groupRelation().selector().nameEq(name).isEmpty
    }

    fun exist(name:String, id: Int): Boolean {
        return !groupRelation().selector().nameEq(name).idNotEq(id).isEmpty
    }

    private fun groupRelation(): Category_Relation {
        return orma.relationOfCategory()
    }

    private fun maxGroupOrder(): Int {
        return groupRelation().selector().maxByViewOrder() ?: 0
    }

}