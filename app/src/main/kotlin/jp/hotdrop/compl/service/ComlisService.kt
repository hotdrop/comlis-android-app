package jp.hotdrop.compl.service

import io.reactivex.Single
import jp.hotdrop.compl.model.ReceiveCompany
import retrofit2.http.GET

interface ComlisService {

    @GET("/companies")
    fun companies(): Single<List<ReceiveCompany>>
}