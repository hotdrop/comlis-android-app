package jp.hotdrop.compl.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {
    fun plus(module: ActivityModule): ActivityComponent
}