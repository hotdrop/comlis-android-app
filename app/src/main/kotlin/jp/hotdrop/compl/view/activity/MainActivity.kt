package jp.hotdrop.compl.view.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityMainBinding
import jp.hotdrop.compl.view.fragment.CategoryFragment
import jp.hotdrop.compl.view.fragment.CompanyFragment

class MainActivity : BaseActivity()
        //, FragmentManager.OnBackStackChangedListener
    {

    //private val EXTRA_MENU = "menu"

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    private lateinit var companyFragment: Fragment
    private lateinit var categoryFragment: Fragment

    //@Inject
    //lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        getComponent().inject(this)

        initView()
        initFragments(savedInstanceState)

        //if(savedInstanceState == null) {
        //    replaceFragment(CompanyFragment.newInstance())
        //} else if(savedInstanceState.getInt(EXTRA_MENU) != 0) {
            //val navPage = NavigationPage.forMenuId(savedInstanceState.getInt(EXTRA_MENU))
            //toggleToolbarElevation(navPage.toggleToolbar)
            //binding.toolbar.setTitle(navPage.titleResId)
        //}
        //supportFragmentManager.addOnBackStackChangedListener(this)
    }

    private fun initView() {
        // droidkaigi2017ではBottomNavigationViewHelperを作っているが・・
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            binding.title.text= item.title
            item.isChecked = true
            when(item.itemId) {
                R.id.nav_companies -> switchFragment(companyFragment, CompanyFragment.TAG)
                R.id.nav_categories -> switchFragment(categoryFragment, CategoryFragment.TAG)
                else -> false
            }
        }
    }

    private fun initFragments(savedInstanceState: Bundle?) {
        companyFragment = supportFragmentManager.findFragmentByTag(CompanyFragment.TAG) ?: CompanyFragment.newInstance()
        categoryFragment = supportFragmentManager.findFragmentByTag(CategoryFragment.TAG) ?: CategoryFragment.newInstance()

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

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        //val currentFragment = supportFragmentManager.findFragmentById(R.id.content_view)
        //if(currentFragment != null) {
        //    outState!!.putInt(EXTRA_MENU, NavigationPage.forName(currentFragment).menuId)
        //}
    }

    override fun onBackPressed() {
        //if(binding.drawer.isDrawerOpen(GravityCompat.START)) {
        //    binding.drawer.closeDrawer(GravityCompat.START)
         //   return
        //}
        //val fm = supportFragmentManager
        //if(fm.backStackEntryCount > 0) {
        //    fm.popBackStack()
        //    return
        //}
        if(switchFragment(companyFragment, CompanyFragment.TAG)) {
            binding.bottomNav.menu.findItem(R.id.nav_companies).isChecked = true
            binding.title.text = getString(R.string.companies)
        }
        super.onBackPressed()
    }

    /**
     * onBackPressedやreplaceFragmentはフラグメントの操作しか行わないため
     * バックスタックに変更があった場合、ハンドリングしてナビゲーションビューや
     * ツールバーなどのコントロールを変更する。
     */
    /*
    override fun onBackStackChanged() {
        // TODO onBackStackChangedがなぜか二回呼ばれてしまうのでなんか考える
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_view) ?: return
        val navPage = NavigationPage.forName(currentFragment)
        binding.navView.setCheckedItem(navPage.menuId)
        binding.toolbar.setTitle(navPage.titleResId)
        toggleToolbarElevation(navPage.toggleToolbar)

        currentFragment as? StackedPageListener ?: return
        currentFragment.onTop()
    }*/

    /*
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawer.closeDrawer(GravityCompat.START)
        val navPage = NavigationPage.forMenuId(item)
        toggleToolbarElevation(navPage.toggleToolbar)
        changePage(navPage.titleResId, navPage.createFragment())
        return true
    }*/

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_fade_enter, R.anim.activity_fade_exit)
    }

    override fun onDestroy() {
        super.onDestroy()
        //supportFragmentManager.removeOnBackStackChangedListener(this)
        //compositeDisposable.dispose()
    }

    private fun toggleToolbarElevation(enable: Boolean) {
        val elevation = if(enable) resources.getDimension(R.dimen.elevation) else 0.toFloat()
        binding.toolbar.elevation = elevation
    }

    private fun changePage(@StringRes titleRes: Int, fragment: Fragment) {
        Handler().postDelayed(fun() {
            binding.toolbar.setTitle(titleRes)
            replaceFragment(fragment)
        }, 100)
    }
}