package jp.hotdrop.compl.di

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides

@Module
class FragmentModule(fragment: Fragment) {

    private val fragment = fragment

    @Provides
    fun provideFragmentManager(): FragmentManager {
        return fragment.fragmentManager
    }
}