package jp.hotdrop.compl.dao

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.model.Tag_Relation
import java.util.*

object TagDao {

    var orma = OrmaHolder.buildDB

    fun find(name: String): Tag {
        return tagRelation().selector()
                .nameEq(name)
                .first()
    }

    fun findAll(): Single<List<Tag>> {
        return tagRelation().selector()
                .orderByViewOrderAsc()
                .executeAsObservable()
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun insert(argName: String, argColorType: String) {
        val tag = Tag().apply {
            name = argName
            colorType = argColorType
            viewOrder = maxOrder() + 1
            registerDate = Date(System.currentTimeMillis())
        }
        orma.transactionSync {
            tagRelation().inserter().execute(tag)
        }
    }

    fun update(tag: Tag) {
        orma.transactionSync {
            tagRelation().updater()
                    .name(tag.name)
                    .colorType(tag.colorType)
                    .updateDate(Date(System.currentTimeMillis()))
                    .idEq(tag.id)
                    .execute()
        }
    }

    fun updateAllOrder(tags: List<Tag>) {
        orma.transactionSync {
            for((index, tag) in tags.withIndex()) {
                tagRelation().updater()
                        .viewOrder(index)
                        .idEq(tag.id)
                        .execute()
            }
        }
    }

    fun delete(tag: Tag) {
        orma.transactionSync {
            tagRelation().deleter()
                    .idEq(tag.id)
                    .execute()
        }
    }

    fun exist(name: String, id: Int = -1): Boolean {
        if(id == -1) {
            return !tagRelation().selector()
                    .nameEq(name)
                    .isEmpty
        } else {
            return !tagRelation().selector()
                    .nameEq(name)
                    .idNotEq(id)
                    .isEmpty
        }
    }

    private fun maxOrder(): Int {
        return tagRelation().selector().maxByViewOrder() ?: 0
    }

    private fun tagRelation(): Tag_Relation {
        return orma.relationOfTag()
    }
}