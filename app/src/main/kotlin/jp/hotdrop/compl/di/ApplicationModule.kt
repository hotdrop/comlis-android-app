package jp.hotdrop.compl.di

import android.app.Application
import android.content.Context
import com.github.gfx.android.orma.AccessThreadConstraint
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import jp.hotdrop.compl.dao.OrmaHolder
import jp.hotdrop.compl.model.OrmaDatabase

@Module
class ApplicationModule(private val appContext: Application) {

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
}