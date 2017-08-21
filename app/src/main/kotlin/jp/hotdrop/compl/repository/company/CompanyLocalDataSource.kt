package jp.hotdrop.compl.repository.company

import io.reactivex.Single
import jp.hotdrop.compl.model.AssociateCompanyWithTag
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.repository.OrmaHolder
import jp.hotdrop.compl.repository.tag.TagRepository
import java.util.Date
import javax.inject.Inject

class CompanyLocalDataSource @Inject constructor(ormaHolder: OrmaHolder,
                                                 private val tagRepository: TagRepository)  {

    private val orma = ormaHolder.orma

    fun find(id: Int) =
            companyRelation().selector()
                    .idEq(id)
                    .value()

    // 登録した会社はリストの先頭に表示したい。
    // でもviewOrderを負の値で登にするのが嫌だったので、viewOrderは普通にインクリメントし
    // ViewOrderDescで取得する。
    fun findAll(): Single<List<Company>> =
            companyRelation().selector()
                    .orderByViewOrderDesc()
                    .executeAsObservable()
                    .toList()

    fun findByCategory(categoryId: Int): Single<List<Company>> =
            companyRelation().selector()
                    .categoryIdEq(categoryId)
                    .orderByViewOrderDesc()
                    .executeAsObservable()
                    .toList()

    fun findByTag(companyId: Int): List<Tag> {
        val associateTags = associateCompanyAndTagRelation()
                .selector()
                .companyIdEq(companyId)
        val tagIds = associateTags.map { it.tagId }
        return tagRepository.findInIds(tagIds)
    }

    fun countByCategory(categoryId: Int) =
            companyRelation().selector()
                    .categoryIdEq(categoryId)
                    .count()

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
            companyIds.forEachIndexed { index, id ->
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

    fun hasAssociateTag(companyId: Int, tagId: Int) =
            !associateCompanyAndTagRelation().selector()
                    .companyIdEq(companyId)
                    .tagIdEq(tagId)
                    .isEmpty

    fun exist(name: String) =
            !companyRelation().selector()
                    .nameEq(name)
                    .isEmpty

    fun existExclusionId(name: String, id: Int) =
            !companyRelation().selector()
                    .nameEq(name)
                    .idNotEq(id)
                    .isEmpty

    private fun maxOrder() =
            companyRelation().selector()
                    .maxByViewOrder() ?: 0

    private fun companyRelation() =
            orma.relationOfCompany()

    private fun associateCompanyAndTagRelation() =
            orma.relationOfAssociateCompanyWithTag()
}