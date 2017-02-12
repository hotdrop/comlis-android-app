package jp.hotdrop.compl.di

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class FragmentModule @Inject constructor(private var fragment: Fragment) {

    @Provides
    fun provideFragmentManager(): FragmentManager = fragment.fragmentManager
}