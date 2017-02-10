package jp.hotdrop.compl.di

import android.support.v7.app.AppCompatActivity
import dagger.Module

@Module
class ActivityModule(activity: AppCompatActivity) {
    val activity = activity

    fun activity(): AppCompatActivity {
        return activity
    }
}