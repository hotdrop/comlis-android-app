package jp.hotdrop.compl.dao

import io.reactivex.Single
import jp.hotdrop.compl.model.Group
import jp.hotdrop.compl.model.Group_Relation

object GroupDao {

    var orma = OrmaHolder.ORMA

    fun insert(group: Group) {
        groupRelation().inserter().execute(group)
    }

    fun findAll(): Single<List<Group>> {
        return groupRelation().selector()
                .executeAsObservable()
                .toList()
    }

    private fun groupRelation(): Group_Relation {
        return orma.relationOfGroup()
    }

}