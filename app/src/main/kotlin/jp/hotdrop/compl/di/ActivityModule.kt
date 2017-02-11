package jp.hotdrop.compl.di

import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class ActivityModule @Inject constructor(var activity: AppCompatActivity) {

    @Provides
    fun activity(): AppCompatActivity = activity
}