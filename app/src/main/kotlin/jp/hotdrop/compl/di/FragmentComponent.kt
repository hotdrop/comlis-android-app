package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.di.scope.FragmentScope
import jp.hotdrop.compl.view.fragment.*

@FragmentScope
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {
    fun inject(fragment: CompanyFragment)
    fun inject(fragment: CompanyTabFragment)
    fun inject(fragment: CompanyRegisterFragment)
    fun inject(fragment: CompanyDetailFragment)
    fun inject(fragment: CompanyAssociateTagFragment)
    fun inject(fragment: CompanyEditFragment)
    fun inject(fragment: CategoryFragment)
    fun inject(fragment: TagFragment)
    fun inject(fragment: TagViewOrderFragment)
}