package jp.hotdrop.compl.view

import android.support.v4.app.Fragment
import android.view.MenuItem
import jp.hotdrop.compl.R
import jp.hotdrop.compl.view.fragment.CategoryFragment
import jp.hotdrop.compl.view.fragment.CompanyFragment

enum class NavigationPage(val menuId: Int, val titleResId: Int, val toggleToolbar: Boolean, val pageName: String) {

    COMPANY_LIST(R.id.nav_main_list, R.string.nav_company_list, false, CompanyFragment.TAG) {
        override fun createFragment(): Fragment {
            return CompanyFragment.newInstance()
        }
    },
    GROUP_LIST(R.id.nav_group_list, R.string.nav_category_list, true, CategoryFragment.TAG) {
        override fun createFragment(): Fragment {
            return CategoryFragment.newInstance()
        }
    };

    abstract fun createFragment(): Fragment

    companion object {

        /**
         * eumnのオブジェクトを取得する
         */
        fun forMenuId(item: MenuItem): NavigationPage {
            return forMenuId(item.itemId)
        }

        fun forMenuId(id: Int): NavigationPage {
            values().filter { it.menuId == id }
                    .forEach { return it }
            throw AssertionError("Error!! menuId not found in enum.")
        }

        fun forName(fragment: Fragment): NavigationPage {
            val name = fragment.javaClass.simpleName
            values().filter { it.pageName == name }
                    .forEach { return it }
            throw AssertionError("Error!! fragment class name not found in enum.")
        }
    }
}