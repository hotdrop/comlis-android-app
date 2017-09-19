package jp.hotdrop.comlis.di

import dagger.Subcomponent
import jp.hotdrop.comlis.di.scope.FragmentScope
import jp.hotdrop.comlis.view.fragment.*

@FragmentScope
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {
    fun inject(fragment: CompanyRootFragment)
    fun inject(fragment: CompanyTabFragment)
    fun inject(fragment: CompanyRegisterFragment)
    fun inject(fragment: CompanyDetailFragment)
    fun inject(fragment: CompanyAssociateTagFragment)
    fun inject(fragment: CompanyEditOverviewFragment)
    fun inject(fragment: CompanyEditInfoFragment)
    fun inject(fragment: CompanyEditBusinessFragment)
    fun inject(fragment: CompanyJobEvaluationFragment)
    fun inject(fragment: CompanyEditDescriptionFragment)
    fun inject(fragment: CategoryFragment)
    fun inject(fragment: TagFragment)
    fun inject(fragment: SearchFragment)
}