package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.view.activity.CompanyRegisterActivity
import jp.hotdrop.compl.view.activity.MainActivity
import jp.hotdrop.compl.di.scope.ActivityScope

@ActivityScope
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(activity: MainActivity)

    fun inject(activity: CompanyRegisterActivity)

    fun plus(module: FragmentModule): FragmentComponent
}