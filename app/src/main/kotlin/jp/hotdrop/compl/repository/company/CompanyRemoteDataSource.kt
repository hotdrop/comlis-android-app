package jp.hotdrop.compl.repository.company

import io.reactivex.Single
import jp.hotdrop.compl.model.ReceiveCompany
import jp.hotdrop.compl.service.ComlisService
import javax.inject.Inject

class CompanyRemoteDataSource @Inject constructor(
        private val comlisService: ComlisService
) {
    fun findAll(): Single<List<ReceiveCompany>> = comlisService.companies()
}