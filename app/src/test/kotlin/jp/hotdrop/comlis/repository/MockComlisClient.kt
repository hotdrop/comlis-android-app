package jp.hotdrop.comlis.repository

import jp.hotdrop.comlis.service.ComlisService
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MockComlisClient(
        private val url: HttpUrl? = null
) {
    private val httpClient = OkHttpClient.Builder().build()
    private val mockRetrofit: Retrofit by lazy {
        // if you can test to the remote repository in the mock server,
        // you will setting the mock server URL,
        // otherwise you will setting nothing, make it a dummy url.
        if(url == null) {
            Retrofit.Builder()
                    .client(httpClient)
                    .baseUrl("https://test.test")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        } else {
            Retrofit.Builder()
                    .client(httpClient)
                    .baseUrl(url)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }
    }

    fun service(): ComlisService = mockRetrofit.create(ComlisService::class.java)
}