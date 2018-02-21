package jp.hotdrop.comlis.di

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import dagger.Module
import dagger.Provides

@Module
class FragmentModule constructor(
        private var fragment: Fragment
) {
    // これいらないんじゃないのか・・FragmentManager使ってない・・
    @Provides
    fun provideFragmentManager(): FragmentManager = fragment.fragmentManager
}