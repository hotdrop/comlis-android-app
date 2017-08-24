package jp.hotdrop.compl.repository.company

import io.reactivex.Single
import jp.hotdrop.compl.api.ComlisClient
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class CompanyRemoteDataSource @Inject constructor(private val client: ComlisClient) {

    fun findAll(): Single<List<Company>> = client.getCompanies()
}