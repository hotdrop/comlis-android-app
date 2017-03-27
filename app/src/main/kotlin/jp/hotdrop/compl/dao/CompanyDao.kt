package jp.hotdrop.compl.dao

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Company_Relation

object CompanyDao {

    var orma = OrmaHolder.ORMA

    fun insert(company: Company) {
        company.viewOrder = maxOrder() + 1
        companyRelation().inserter().execute(company)
    }

    fun updateFavorite(id: Int, favorite: Int) {
        companyRelation().updater()
                .favorite(favorite)
                .idEq(id)
                .execute()
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

    fun find(id: Int): Company {
        return companyRelation().selector().idEq(id).first()
    }

    fun countByCategory(categoryId: Int): Int {
        return companyRelation().selector().categoryIdEq(categoryId).count()
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