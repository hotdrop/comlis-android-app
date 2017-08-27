package jp.hotdrop.compl.api.service

import io.reactivex.Single
import jp.hotdrop.compl.model.ReceiveCompany
import retrofit2.http.GET

interface CdsService {

    @GET("/companies")
    fun companies(): Single<List<ReceiveCompany>>
}