package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.di.scope.FragmentScope
import jp.hotdrop.compl.fragment.CompanyFragment
import jp.hotdrop.compl.fragment.CompanyRegisterFragment

@FragmentScope
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(fragment: CompanyFragment)

    fun inject(fragment: CompanyRegisterFragment)
}