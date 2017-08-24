package jp.hotdrop.compl.api.service

import io.reactivex.Single
import jp.hotdrop.compl.model.Company
import retrofit2.http.GET

interface CdsService {

    @GET("/companies")
    fun getCompanies(): Single<List<Company>>
}