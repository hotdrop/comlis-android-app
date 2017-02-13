package jp.hotdrop.compl.dao

import io.reactivex.Single
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Company_Relation
import jp.hotdrop.compl.model.OrmaDatabase

object CompanyDao {

    var orma: OrmaDatabase = OrmaHolder.ORMA

    fun insert(company: Company) {
        companyRelation().inserter().execute(company)
    }

    fun findAll(): Single<List<Company>> {
        return companyRelation().selector().executeAsObservable().toList()
    }

    private fun companyRelation(): Company_Relation {
        return orma.relationOfCompany()
    }

}