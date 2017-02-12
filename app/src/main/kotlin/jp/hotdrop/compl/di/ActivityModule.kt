package jp.hotdrop.compl.di

import android.content.Context
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class ActivityModule @Inject constructor(private var activity: AppCompatActivity) {

    @Provides
    fun activity(): AppCompatActivity = activity

    @Provides
    fun context(): Context = activity
}