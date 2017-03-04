package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.di.scope.FragmentScope
import jp.hotdrop.compl.view.fragment.CompanyFragment
import jp.hotdrop.compl.view.fragment.CompanyRegisterFragment
import jp.hotdrop.compl.view.fragment.CompanyTabFragment
import jp.hotdrop.compl.view.fragment.CategoryFragment

@FragmentScope
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(fragment: CompanyFragment)

    fun inject(fragment: CompanyTabFragment)

    fun inject(fragment: CompanyRegisterFragment)

    fun inject(fragment: CategoryFragment)
}