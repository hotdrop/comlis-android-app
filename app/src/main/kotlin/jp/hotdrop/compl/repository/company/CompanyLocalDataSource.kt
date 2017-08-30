package jp.hotdrop.compl.repository.company

import io.reactivex.Single
import jp.hotdrop.compl.model.AssociateCompanyWithTag
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.repository.OrmaHolder
import jp.hotdrop.compl.repository.tag.TagRepository
import java.util.*
import javax.inject.Inject

class CompanyLocalDataSource @Inject constructor(
        ormaHolder: OrmaHolder,
        private val tagRepository: TagRepository
)  {
    private val orma = ormaHolder.orma

    fun find(id: Int) =
            companyRelation().selector()
                    .idEq(id)
                    .value()

    fun find(name: String) =
            companyRelation().selector()
                    .nameEq(name)
                    .value()

    // 登録した会社はリストの先頭に表示したい。
    // 考えた案は2つ、1つは登録時に最新のデータはviewOrderを負の値にする案でもう一つはviewOrderを降順で取得する案
    // 負の値はいくつかバグを生む可能性があったため、2つ目の案を採用した。
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
        executeInsert(company.apply {
            viewOrder = maxOrder() + 1
            registerDate = Date(System.currentTimeMillis())
        })
    }

    fun insertWithRemote(company: Company) {
        executeInsert(company.apply {
            viewOrder = maxOrder() + 1
            val nowDate = Date(System.currentTimeMillis())
            registerDate = nowDate
            fromRemoteDate = nowDate
        })
    }

    private fun executeInsert(company: Company) {
        orma.transactionSync {
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
        executeUpdate(company, null)
    }

    fun updateWithRemote(company: Company) {
        executeUpdate(company, Date(System.currentTimeMillis()))
    }

    private fun executeUpdate(company: Company, fromRemoteDate: Date?) {
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
                    .fromRemoteDate(fromRemoteDate)
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