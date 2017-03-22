package jp.hotdrop.compl.dao

import io.reactivex.Single
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Company_Relation

object CompanyDao {

    var orma = OrmaHolder.ORMA

    fun insert(company: Company) {
        company.order = maxOrder() + 1
        companyRelation().inserter().execute(company)
    }

    fun updateFavorite(company: Company) {
        companyRelation().updater()
                .favorite(company.favorite)
                .idEq(company.id)
                .execute()
    }

    fun findAll(): Single<List<Company>> {
        return companyRelation().selector()
                                .executeAsObservable()
                                .toList()
    }

    fun find(id: Int): Company {
        return companyRelation().selector().idEq(id).first()
    }

    fun countByCategory(categoryId: Int): Int {
        return companyRelation().selector().categoryIdEq(categoryId).count()
    }

    fun updateAllOrder(companies: MutableList<Company>) {
        // TODO Completableにすべき
        for((index, company) in companies.withIndex()) {
            companyRelation().updater()
                    .order(index)
                    .idEq(company.id)
                    .execute()
        }
    }

    private fun companyRelation(): Company_Relation {
        return orma.relationOfCompany()
    }

    private fun maxOrder(): Int {
        return companyRelation().selector().maxByOrder() ?: 0
    }

}