package jp.hotdrop.compl.repository.company

import io.reactivex.Single
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanyRepository @Inject constructor(private val localDataSource: CompanyLocalDataSource,
                                            private val remoteDataSource: CompanyRemoteDataSource,
                                            private val jobEvaluationLocalDataSource: JobEvaluationLocalDataSource) {

    fun find(id: Int) =
            localDataSource.find(id)

    fun findAll(): Single<List<Company>> =
            localDataSource.findAll()

    fun findAllFromRemote(): Single<List<Company>> =
            remoteDataSource.findAll()

    fun findByCategory(categoryId: Int): Single<List<Company>> =
            localDataSource.findByCategory(categoryId)

    fun findByTag(companyId: Int): List<Tag> =
            localDataSource.findByTag(companyId)

    fun countByCategory(categoryId: Int) =
            localDataSource.countByCategory(categoryId)

    fun insert(company: Company) {
        localDataSource.insert(company)
    }

    fun associateTagByCompany(companyId: Int, tags: List<Tag>) {
        localDataSource.associateTagByCompany(companyId, tags)
    }

    fun update(company: Company) {
        localDataSource.update(company)
    }

    fun updateOverview(company: Company) {
        localDataSource.updateOverview(company)
    }

    fun updateInformation(company: Company) {
        localDataSource.updateInformation(company)
    }

    fun updateBusiness(company: Company) {
        localDataSource.updateBusiness(company)
    }

    fun updateDescription(company: Company) {
        localDataSource.updateDescription(company)
    }

    fun updateFavorite(id: Int, favorite: Int) {
        localDataSource.updateFavorite(id, favorite)
    }

    fun updateAllOrder(companyIds: List<Int>) {
        localDataSource.updateAllOrder(companyIds)
    }

    fun delete(companyId: Int) {
        localDataSource.delete(companyId)
    }

    fun hasAssociateTag(companyId: Int, tagId: Int) =
            localDataSource.hasAssociateTag(companyId, tagId)

    fun exist(name: String) =
            localDataSource.exist(name)

    fun existExclusionId(name: String, id: Int) =
            localDataSource.existExclusionId(name, id)

    fun findJobEvaluation(companyId: Int) =
            jobEvaluationLocalDataSource.find(companyId)

    fun upsertJobEvaluation(jobEvaluation: JobEvaluation) {
        jobEvaluationLocalDataSource.upsert(jobEvaluation)
    }
}