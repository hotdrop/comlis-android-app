package jp.hotdrop.compl.dao

import jp.hotdrop.compl.model.Group
import jp.hotdrop.compl.model.Group_Relation

object GroupDao {

    var orma = OrmaHolder.ORMA

    /**
     * 今はGroupNameが重複しない前提としている。あまり良くない・・
     */
    fun find(name: String): Group {
        return groupRelation().selector().nameEq(name).first()
    }

    fun findAll(): MutableList<Group> {
        return groupRelation().selector()
                .orderByViewOrderAsc()
                .toList()
    }

    fun insert(name: String) {
        val group = Group()
        group.name = name
        group.viewOrder = maxGroupOrder() + 1
        groupRelation().inserter().execute(group)
    }

    fun update(group: Group) {
        groupRelation().updater().name(group.name).execute()
    }

    fun exist(name: String): Boolean {
        return !groupRelation().selector().nameEq(name).isEmpty
    }

    fun exist(name:String, id: Int): Boolean {
        return !groupRelation().selector().nameEq(name).idNotEq(id).isEmpty
    }

    private fun groupRelation(): Group_Relation {
        return orma.relationOfGroup()
    }

    private fun maxGroupOrder(): Int {
        return groupRelation().selector().maxByViewOrder() ?: 0
    }

}