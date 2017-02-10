package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.MainActivity
import jp.hotdrop.compl.di.scope.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(activity: MainActivity)
}