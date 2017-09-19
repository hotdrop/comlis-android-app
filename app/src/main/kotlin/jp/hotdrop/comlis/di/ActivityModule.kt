package jp.hotdrop.comlis.di

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides

@Module
class ActivityModule constructor(
        private var activity: AppCompatActivity
) {

    @Provides
    fun provideActivity(): AppCompatActivity = activity
}