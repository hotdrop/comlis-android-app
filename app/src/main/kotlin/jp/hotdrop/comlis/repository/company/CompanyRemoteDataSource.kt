package jp.hotdrop.comlis.repository.company

import io.reactivex.Single
import jp.hotdrop.comlis.model.ReceiveCompany
import jp.hotdrop.comlis.service.ComlisService
import javax.inject.Inject

class CompanyRemoteDataSource @Inject constructor(
        private val comlisService: ComlisService
) {
    fun findAll(latestDateEpoch: Long): Single<List<ReceiveCompany>> =
            comlisService.companies(latestDateEpoch)
}