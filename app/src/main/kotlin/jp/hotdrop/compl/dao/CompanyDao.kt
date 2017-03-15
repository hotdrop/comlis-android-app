package jp.hotdrop.compl.dao

import io.reactivex.Single
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Company_Relation

object CompanyDao {

    var orma = OrmaHolder.ORMA

    fun insert(company: Company) {
        companyRelation().inserter().execute(company)
    }

    fun findAll(): Single<List<Company>> {
        return companyRelation().selector()
                                .executeAsObservable()
                                .toList()
    }

    fun countByCategory(categoryId: Int): Int {
        return companyRelation().selector().categoryIdEq(categoryId).count()
    }

    private fun companyRelation(): Company_Relation {
        return orma.relationOfCompany()
    }

}