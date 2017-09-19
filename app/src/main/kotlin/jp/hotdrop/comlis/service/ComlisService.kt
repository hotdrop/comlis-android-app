package jp.hotdrop.comlis.service

import io.reactivex.Single
import jp.hotdrop.comlis.model.ReceiveCompany
import retrofit2.http.GET

interface ComlisService {

    @GET("/companies")
    fun companies(): Single<List<ReceiveCompany>>
}