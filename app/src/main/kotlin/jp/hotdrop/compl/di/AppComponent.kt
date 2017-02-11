package jp.hotdrop.compl.di

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class))
interface AppComponent {
    fun plus(module: ActivityModule): ActivityComponent
}