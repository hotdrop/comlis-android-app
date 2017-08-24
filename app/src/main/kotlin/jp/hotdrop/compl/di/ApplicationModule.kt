package jp.hotdrop.compl.di

import android.app.Application
import android.content.Context
import com.github.gfx.android.orma.AccessThreadConstraint
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import jp.hotdrop.compl.BuildConfig
import jp.hotdrop.compl.api.service.CdsService
import jp.hotdrop.compl.model.OrmaDatabase
import jp.hotdrop.compl.repository.OrmaHolder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class ApplicationModule(
        private val appContext: Application
) {

    @Provides
    fun provideContext(): Context = appContext

    @Provides
    fun provideOrmaHolder(context: Context): OrmaHolder {
        // can't access NonExistentClassを防ぐためOrmaHolderを間に挟む。
        val orma = OrmaDatabase.builder(context)
                .writeOnMainThread(AccessThreadConstraint.NONE)
                .name("compl.orma.db")
                .build()
        return OrmaHolder(orma)
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

    @Singleton
    @Provides
    fun provideHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Singleton
    @Provides
    fun provideCdsService(client: OkHttpClient): CdsService =
        Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.API_ROOT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CdsService::class.java)
}