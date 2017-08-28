package jp.hotdrop.compl.view.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityMainBinding
import jp.hotdrop.compl.view.fragment.CategoryFragment
import jp.hotdrop.compl.view.fragment.CompanyRootFragment
import jp.hotdrop.compl.view.fragment.TagFragment

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var companyFragment: Fragment
    private lateinit var categoryFragment: Fragment
    private lateinit var tagFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        getComponent().inject(this)

        initView()
        initFragments(savedInstanceState)
    }

    override fun onBackPressed() {
        if(switchFragment(companyFragment, CompanyRootFragment.TAG)) {
            binding.bottomNav.menu.findItem(R.id.nav_companies).isChecked = true
            binding.title.text = getString(R.string.companies)
            return
        }
        super.onBackPressed()
    }

    private fun initView() {
        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            binding.title.text= menuItem.title

            menuItem.isChecked = true

            when(menuItem.itemId) {
                R.id.nav_companies -> switchFragment(companyFragment, CompanyRootFragment.TAG)
                R.id.nav_categories -> switchFragment(categoryFragment, CategoryFragment.TAG)
                R.id.nav_tags -> switchFragment(tagFragment, TagFragment.TAG)
                else -> false
            }
        }
    }

    private fun initFragments(savedInstanceState: Bundle?) {
        companyFragment = supportFragmentManager.findFragmentByTag(CompanyRootFragment.TAG) ?: CompanyRootFragment.newInstance()
        categoryFragment = supportFragmentManager.findFragmentByTag(CategoryFragment.TAG) ?: CategoryFragment.newInstance()
        tagFragment = supportFragmentManager.findFragmentByTag(TagFragment.TAG) ?: TagFragment.newInstance()
        savedInstanceState ?: switchFragment(companyFragment, CompanyRootFragment.TAG)
    }

    private fun switchFragment(fragment: Fragment, sign: String): Boolean {

        if(fragment.isAdded) {
            return false
        }

        val ft = supportFragmentManager.beginTransaction()
        supportFragmentManager.findFragmentById(R.id.content_view)?.let {
            ft.detach(it)
        }

        if(fragment.isDetached) {
            ft.attach(fragment)
        } else {
            ft.add(R.id.content_view, fragment, sign)
        }

        // フラグメントの交換時にフェードイン/アウトをつける
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()

        return true
    }
}