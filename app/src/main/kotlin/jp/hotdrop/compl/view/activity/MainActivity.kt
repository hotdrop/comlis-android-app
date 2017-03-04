package jp.hotdrop.compl.view.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.annotation.StringRes
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import io.reactivex.disposables.CompositeDisposable
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityMainBinding
import jp.hotdrop.compl.view.NavigationPage
import jp.hotdrop.compl.view.PageStateBus
import jp.hotdrop.compl.view.StackedPageListener
import jp.hotdrop.compl.view.fragment.CompanyFragment
import javax.inject.Inject

class MainActivity : BaseActivity(),
        NavigationView.OnNavigationItemSelectedListener, FragmentManager.OnBackStackChangedListener{

    private val EXTRA_MENU = "menu"

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
    }

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.bind<ActivityMainBinding>(binding.navView.getHeaderView(0))
        getComponent().inject(this)
        compositeDisposable.add(PageStateBus.observe()
                                .subscribe { page ->
                                    toggleToolbarElevation(page.toggleToolbar)
                                    changePage(page.titleResId, page.createFragment())
                                    binding.navView.setCheckedItem(page.menuId)
                                })
        initView()
        if(savedInstanceState == null) {
            replaceFragment(CompanyFragment.newInstance())
        } else if(savedInstanceState.getInt(EXTRA_MENU) != 0) {
            val navPage = NavigationPage.forMenuId(savedInstanceState.getInt(EXTRA_MENU))
            toggleToolbarElevation(navPage.toggleToolbar)
            binding.toolbar.setTitle(navPage.titleResId)
        }
        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    private fun initView() {
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.open, R.string.close)
        toggle.syncState()
        binding.drawer.addDrawerListener(toggle)
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null
        binding.navView.setCheckedItem(R.id.nav_main_list)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_view)
        currentFragment ?: outState!!.putInt(EXTRA_MENU, NavigationPage.forName(currentFragment).menuId)
    }

    override fun onBackStackChanged() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.content_view) ?: return
        val navPage = NavigationPage.forName(currentFragment)
        binding.navView.setCheckedItem(navPage.menuId)
        binding.toolbar.setTitle(navPage.titleResId)
        toggleToolbarElevation(navPage.toggleToolbar)
        currentFragment as? StackedPageListener ?: return
        currentFragment.onTop()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawer.closeDrawer(GravityCompat.START)
        val navPage = NavigationPage.forMenuId(item)
        toggleToolbarElevation(navPage.toggleToolbar)
        changePage(navPage.titleResId, navPage.createFragment())
        return true
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_fade_enter, R.anim.activity_fade_exit)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.removeOnBackStackChangedListener(this)
        compositeDisposable.dispose()
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