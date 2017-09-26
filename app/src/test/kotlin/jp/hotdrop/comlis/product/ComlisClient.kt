package jp.hotdrop.comlis.product

import jp.hotdrop.comlis.BuildConfig
import jp.hotdrop.comlis.service.ComlisService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class ComlisClient {

    // If you used self signed certificate, you changed follow passStr.
    private val KEY_PASS = "serverpass"
    private val KEY_STORE_PASS = "keypass"

    private val okHttpClient = createOkHttpClient()

    private val retrofit: Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.API_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    fun service(): ComlisService =
            retrofit.create(ComlisService::class.java)

    private fun createOkHttpClient(): OkHttpClient {
        val keyStore = readKeyStore()

        val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore)
        }
        val trustManager = (trustManagerFactory.trustManagers.first() as? X509TrustManager) ?:
                throw IllegalStateException("Unexpected default trust managers.")

        val keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()).apply {
            init(keyStore, KEY_PASS.toCharArray())
        }

        val sslContext = SSLContext.getInstance("TLS").apply {
            init(keyManagerFactory.keyManagers, arrayOf(trustManager), null)
        }

        return OkHttpClient.Builder()
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { _, _ -> true }
                .build()
    }

    private fun readKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
        val inputStream = javaClass.classLoader.getResourceAsStream("server.keystore")
        inputStream.use {
            keyStore.load(it, KEY_STORE_PASS.toCharArray())
        }
        return keyStore
    }
}