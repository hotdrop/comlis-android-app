package jp.hotdrop.compl.dao

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Company_Relation

object CompanyDao {

    var orma = OrmaHolder.ORMA

    fun find(id: Int): Company {
        return companyRelation().selector().idEq(id).first()
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

    fun countByCategory(categoryId: Int): Int {
        return companyRelation().selector().categoryIdEq(categoryId).count()
    }

    fun insert(company: Company) {
        company.viewOrder = maxOrder() + 1
        companyRelation().inserter().execute(company)
    }

    fun update(company: Company) {
        // TODO 実験。upserterはindexedじゃない項目も更新してくれるのか？
        companyRelation().upserter().execute(company)
    }

    fun updateFavorite(id: Int, favorite: Int) {
        companyRelation().updater()
                .favorite(favorite)
                .idEq(id)
                .execute()
    }

    fun updateAllOrder(companies: List<Company>) {
        for((index, company) in companies.withIndex()) {
            companyRelation().updater()
                    .viewOrder(index)
                    .idEq(company.id)
                    .execute()
        }
    }

    private fun companyRelation(): Company_Relation {
        return orma.relationOfCompany()
    }

    private fun maxOrder(): Int {
        return companyRelation().selector().maxByViewOrder() ?: 0
    }

}