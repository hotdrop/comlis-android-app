package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.di.scope.ActivityScope
import jp.hotdrop.compl.view.activity.CompanyDetailActivity
import jp.hotdrop.compl.view.activity.CompanyEditActivity
import jp.hotdrop.compl.view.activity.CompanyRegisterActivity
import jp.hotdrop.compl.view.activity.MainActivity

@ActivityScope
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: CompanyRegisterActivity)
    fun inject(activity: CompanyDetailActivity)
    fun inject(activity: CompanyEditActivity)

    fun plus(module: FragmentModule): FragmentComponent
}