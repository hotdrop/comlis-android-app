package jp.hotdrop.compl.dao

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.model.*
import java.util.*

object CompanyDao {

    var orma = OrmaHolder.buildDB

    fun find(id: Int): Company {
        return companyRelation().selector()
                .idEq(id)
                .first()
    }

    fun findAll(): Single<List<Company>> {
        return companyRelation().selector()
                .executeAsObservable()
                .toList()
    }

    fun findByCategory(categoryId: Int): Single<List<Company>> {
        return companyRelation().selector()
                .categoryIdEq(categoryId)
                .orderByViewOrderAsc()
                .executeAsObservable()
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun findByTag(companyId: Int): List<Tag> {
        val tagIds = associateCompanyAndTagRelation()
                .selector()
                .companyIdEq(companyId)
        return TagDao.findInId(tagIds.map { it.tagId })
    }

    fun countByCategory(categoryId: Int): Int {
        return companyRelation().selector()
                .categoryIdEq(categoryId)
                .count()
    }

    fun insert(company: Company) {
        orma.transactionSync {
            company.viewOrder = maxOrder() + 1
            company.registerDate = Date(System.currentTimeMillis())
            companyRelation().inserter()
                    .execute(company)
        }
    }

    fun associateTagByCompany(argCompanyId: Int, tags: List<Tag>) {
        orma.transactionSync {
            associateCompanyAndTagRelation().deleter()
                    .companyIdEq(argCompanyId)
                    .execute()
            tags.forEach {
                associateCompanyAndTagRelation().inserter()
                        .execute(AssociateCompanyWithTag().apply {
                                companyId = argCompanyId
                                tagId = it.id
                            })
            }
        }
    }

    fun update(company: Company) {
        orma.transactionSync {
            companyRelation().updater()
                    .name(company.name)
                    .categoryId(company.categoryId)
                    .overview(company.overview)
                    .employeesNum(company.employeesNum)
                    .salaryLow(company.salaryLow)
                    .salaryHigh(company.salaryHigh)
                    .wantedJob((company.wantedJob))
                    .workPlace(company.workPlace)
                    .doingBusiness(company.doingBusiness)
                    .wantBusiness(company.wantBusiness)
                    .url(company.url)
                    .note(company.note)
                    .updateDate(Date(System.currentTimeMillis()))
                    .idEq(company.id)
                    .execute()
        }
    }

    fun updateFavorite(id: Int, favorite: Int) {
        orma.transactionSync {
            companyRelation().updater()
                    .favorite(favorite)
                    .idEq(id)
                    .execute()
        }
    }

    fun updateAllOrder(companies: List<Company>) {
        orma.transactionSync {
            for((index, company) in companies.withIndex()) {
                companyRelation().updater()
                        .viewOrder(index)
                        .idEq(company.id)
                        .execute()
            }
        }
    }

    fun delete(company: Company) {
        orma.transactionSync {
            associateCompanyAndTagRelation().deleter()
                    .companyIdEq(company.id)
                    .execute()
            companyRelation().deleter()
                    .idEq(company.id)
                    .execute()

        }
    }

    fun hasAssociateTag(companyId: Int, tagId: Int): Boolean {
        return !associateCompanyAndTagRelation().selector()
                .companyIdEq(companyId)
                .tagIdEq(tagId)
                .isEmpty
    }

    fun maxOrder(): Int {
        return companyRelation().selector().maxByViewOrder() ?: 0
    }

    private fun companyRelation(): Company_Relation {
        return orma.relationOfCompany()
    }

    private fun associateCompanyAndTagRelation(): AssociateCompanyWithTag_Relation {
        return orma.relationOfAssociateCompanyWithTag()
    }
}