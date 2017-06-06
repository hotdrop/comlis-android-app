package jp.hotdrop.compl.dao

import io.reactivex.Single
import jp.hotdrop.compl.model.AssociateCompanyWithTag_Relation
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.model.Tag_Relation
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagDao @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma

    fun find(name: String): Tag {
        return tagRelation().selector()
                .nameEq(name)
                .first()
    }

    fun findInId(ids: List<Int>): List<Tag> {
        return tagRelation().selector()
                .idIn(ids)
                .orderByViewOrderAsc()
                .toList()
    }

    fun findAll(): Single<List<Tag>> {
        return tagRelation().selector()
                .orderByViewOrderAsc()
                .executeAsObservable()
                .toList()
    }

    fun countByAttachCompany(tag: Tag): Int {
        return associateCompanyAndTagRelation().selector()
                .tagIdEq(tag.id)
                .count()
    }

    fun insert(argTag: Tag) {
        val tag = Tag().apply {
            name = argTag.name
            colorType = argTag.colorType
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

    fun exist(name: String): Boolean {
        return !tagRelation().selector()
                .nameEq(name)
                .isEmpty
    }

    fun existExclusionId(name: String, id: Int): Boolean {
        return !tagRelation().selector()
                .nameEq(name)
                .idNotEq(id)
                .isEmpty
    }

    private fun maxOrder(): Int {
        return tagRelation().selector().maxByViewOrder() ?: 0
    }

    private fun tagRelation(): Tag_Relation {
        return orma.relationOfTag()
    }

    private fun associateCompanyAndTagRelation(): AssociateCompanyWithTag_Relation {
        return orma.relationOfAssociateCompanyWithTag()
    }
}