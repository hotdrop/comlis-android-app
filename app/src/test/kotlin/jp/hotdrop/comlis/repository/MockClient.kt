package jp.hotdrop.comlis.repository

import android.security.NetworkSecurityPolicy
import jp.hotdrop.comlis.BuildConfig
import jp.hotdrop.comlis.service.ComlisService
import okhttp3.OkHttpClient
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MockClient {

    // This class is terrible.
    // Should be in fact implements Client Class, and make some mock method.

    // But, now I want to try to connect remote server with same implementation as main so I made it the
    // same as the implementation of ApplicationModule.
    private val httpClient = OkHttpClient.Builder().build()
    private val mockRetrofit: Retrofit = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun create(): ComlisService =
            mockRetrofit.create(ComlisService::class.java)

    /**
     * Test Mock Server is http. not good...
     */
    @Implements(NetworkSecurityPolicy::class)
    class MyNetworkSecurityPolicy {

        companion object {
            @Implementation
            @JvmStatic fun getInstance(): NetworkSecurityPolicy {
                val shadow = MyNetworkSecurityPolicy::class.java.classLoader.loadClass("android.security.NetworkSecurityPolicy")
                return (shadow.newInstance() as NetworkSecurityPolicy)
            }
        }

        @Implementation
        fun isCleartextTrafficPermitted() = true
    }
}