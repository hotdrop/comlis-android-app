package jp.hotdrop.compl.dao

import io.reactivex.Maybe
import io.reactivex.Single
import jp.hotdrop.compl.model.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyDao @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma
    @Inject
    lateinit var tagDao: TagDao

    fun find(id: Int): Maybe<Company> {
        return companyRelation().selector()
                .idEq(id)
                .executeAsObservable()
                .firstElement()
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
    }

    fun findByTag(companyId: Int): List<Tag> {
        val tagIds = associateCompanyAndTagRelation()
                .selector()
                .companyIdEq(companyId)
        return tagDao.findInId(tagIds.map { it.tagId })
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

    fun updateOverview(company: Company) {
        orma.transactionSync {
            companyRelation().updater()
                    .name(company.name)
                    .categoryId(company.categoryId)
                    .overview(company.overview)
                    .updateDate(Date(System.currentTimeMillis()))
                    .idEq(company.id)
                    .execute()
        }
    }

    fun updateInformation(company: Company) {
        orma.transactionSync {
            companyRelation().updater()
                    .employeesNum(company.employeesNum)
                    .salaryLow(company.salaryLow)
                    .salaryHigh(company.salaryHigh)
                    .wantedJob((company.wantedJob))
                    .workPlace(company.workPlace)
                    .url(company.url)
                    .updateDate(Date(System.currentTimeMillis()))
                    .idEq(company.id)
                    .execute()
        }
    }

    fun updateBusiness(company: Company) {
        orma.transactionSync {
            companyRelation().updater()
                    .doingBusiness(company.doingBusiness)
                    .wantBusiness(company.wantBusiness)
                    .updateDate(Date(System.currentTimeMillis()))
                    .idEq(company.id)
                    .execute()
        }
    }

    fun updateDescription(company: Company) {
        orma.transactionSync {
            companyRelation().updater()
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

    fun updateAllOrder(companyIds: List<Int>) {
        orma.transactionSync {
            for((index, id) in companyIds.withIndex()) {
                companyRelation().updater()
                        .viewOrder(index)
                        .idEq(id)
                        .execute()
            }
        }
    }

    fun delete(companyId: Int) {
        orma.transactionSync {
            associateCompanyAndTagRelation().deleter()
                    .companyIdEq(companyId)
                    .execute()
            companyRelation().deleter()
                    .idEq(companyId)
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

    fun exist(name: String): Boolean {
        return !companyRelation().selector()
                .nameEq(name)
                .isEmpty
    }

    fun existExclusionId(name: String, id: Int): Boolean {
        return !companyRelation().selector()
                .nameEq(name)
                .idNotEq(id)
                .isEmpty
    }

    private fun companyRelation(): Company_Relation {
        return orma.relationOfCompany()
    }

    private fun associateCompanyAndTagRelation(): AssociateCompanyWithTag_Relation {
        return orma.relationOfAssociateCompanyWithTag()
    }
}