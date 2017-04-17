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
class AppModule(app: Application) {

    private var context: Context = app

    @Provides
    fun provideContext(): Context = context

    @Provides
    fun provideOrma(context: Context): OrmaHolder {
        // can't access NonExistentClassを防ぐためOrmaHolderを間に挟む。
        val orma = OrmaDatabase.builder(context).writeOnMainThread(AccessThreadConstraint.NONE).build()
        return OrmaHolder(orma)
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}