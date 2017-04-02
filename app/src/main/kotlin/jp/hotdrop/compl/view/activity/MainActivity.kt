package jp.hotdrop.compl.view.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityMainBinding
import jp.hotdrop.compl.view.fragment.CategoryFragment
import jp.hotdrop.compl.view.fragment.CompanyFragment
import jp.hotdrop.compl.view.fragment.TagFragment

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var companyFragment: Fragment
    private lateinit var categoryFragment: Fragment
    private lateinit var tagFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        getComponent().inject(this)

        initView()
        initFragments(savedInstanceState)
    }

    private fun initView() {
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            binding.title.text= item.title
            item.isChecked = true
            when(item.itemId) {
                R.id.nav_companies -> switchFragment(companyFragment, CompanyFragment.TAG)
                R.id.nav_categories -> switchFragment(categoryFragment, CategoryFragment.TAG)
                R.id.nav_tags -> switchFragment(tagFragment, TagFragment.TAG)
                else -> false
            }
        }
    }

    private fun initFragments(savedInstanceState: Bundle?) {
        companyFragment = supportFragmentManager.findFragmentByTag(CompanyFragment.TAG) ?: CompanyFragment.newInstance()
        categoryFragment = supportFragmentManager.findFragmentByTag(CategoryFragment.TAG) ?: CategoryFragment.newInstance()
        tagFragment = supportFragmentManager.findFragmentByTag(TagFragment.TAG) ?: TagFragment.newInstance()

        if(savedInstanceState == null) {
            switchFragment(companyFragment, CompanyFragment.TAG)
        }
    }

    private fun switchFragment(fragment: Fragment, tag: String): Boolean {
        if(fragment.isAdded) {
            return false
        }

        val ft = supportFragmentManager.beginTransaction()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_view)
        if(currentFragment != null) {
            ft.detach(currentFragment)
        }
        if(fragment.isDetached) {
            ft.attach(fragment)
        } else {
            ft.add(R.id.content_view, fragment, tag)
        }
        // フラグメントの交換時にフェードイン/アウトをつける
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()

        return true
    }

    override fun onBackPressed() {
        if(switchFragment(companyFragment, CompanyFragment.TAG)) {
            binding.bottomNav.menu.findItem(R.id.nav_companies).isChecked = true
            binding.title.text = getString(R.string.companies)
            return
        }
        super.onBackPressed()
    }
}