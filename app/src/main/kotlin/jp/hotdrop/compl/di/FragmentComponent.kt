package jp.hotdrop.compl.di

import dagger.Subcomponent
import jp.hotdrop.compl.di.scope.FragmentScope
import jp.hotdrop.compl.view.fragment.CompanyFragment
import jp.hotdrop.compl.view.fragment.CompanyRegisterFragment
import jp.hotdrop.compl.view.fragment.GroupFragment
import jp.hotdrop.compl.view.fragment.GroupRegisterFragment

@FragmentScope
@Subcomponent(modules = arrayOf(FragmentModule::class))
interface FragmentComponent {

    fun inject(fragment: CompanyFragment)

    fun inject(fragment: CompanyRegisterFragment)

    fun inject(fragment: GroupFragment)

    fun inject(fragment: GroupRegisterFragment)
}