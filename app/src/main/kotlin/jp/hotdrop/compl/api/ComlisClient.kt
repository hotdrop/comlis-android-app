package jp.hotdrop.compl.api

import io.reactivex.Single
import jp.hotdrop.compl.api.service.CdsService
import jp.hotdrop.compl.model.Company
import javax.inject.Inject

class ComlisClient @Inject constructor(private val cdsService: CdsService) {
    fun getCompanies(): Single<List<Company>> = cdsService.getCompanies()
}