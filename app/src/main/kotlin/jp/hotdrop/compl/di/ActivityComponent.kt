package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.di.scope.ActivityScope
import jp.hotdrop.compl.view.activity.*

@ActivityScope
@Subcomponent(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(activity: MainActivity)
    fun inject(activity: CompanyRegisterActivity)
    fun inject(activity: CompanyDetailActivity)
    fun inject(activity: CompanyAssociateTagActivity)
    fun inject(activity: CompanyEditOverviewActivity)
    fun inject(activity: CompanyEditInfoActivity)
    fun inject(activity: CompanyEditBusinessActivity)
    fun inject(activity: CompanyEditDescriptionActivity)

    fun inject(activity: SearchActivity)

    fun plus(module: FragmentModule): FragmentComponent
}