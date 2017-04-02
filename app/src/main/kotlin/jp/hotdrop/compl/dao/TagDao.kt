package jp.hotdrop.compl.dao

import android.support.annotation.ColorRes
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.model.Tag_Relation
import java.sql.Date

object TagDao {

    var orma = OrmaHolder.buildDB

    fun findAll(): Single<List<Tag>> {
        return tagRelation().selector()
                .orderByViewOrderAsc()
                .executeAsObservable()
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun insert(argName: String, @ColorRes argColor: Int) {
        val tag = Tag().apply {
            name = argName
            colorRes = argColor
            viewOrder = maxOrder() + 1
            registerDate = Date(System.currentTimeMillis())
        }
        orma.transactionSync {
            tagRelation().inserter().execute(tag)
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

    private fun maxOrder(): Int {
        return tagRelation().selector().maxByViewOrder() ?: 0
    }

    private fun tagRelation(): Tag_Relation {
        return orma.relationOfTag()
    }
}