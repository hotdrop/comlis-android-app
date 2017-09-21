package jp.hotdrop.comlis.service

import io.reactivex.Single
import jp.hotdrop.comlis.model.ReceiveCompany
import retrofit2.http.GET
import retrofit2.http.Query

interface ComlisService {

    @GET("/companies")
    fun companies(@Query("fromDateEpoch") latestDateEpoch: Long): Single<List<ReceiveCompany>>
}