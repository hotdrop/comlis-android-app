package jp.hotdrop.compl.activity

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.FragmentManager
import android.view.MenuItem
import jp.hotdrop.compl.databinding.ActivityMainBinding

class MainActivity : BaseActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener{

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        //binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        //DataBindingUtil.bind<ActivityMainBinding>(binding.navView.getHeaderView(0))
        //getComponent().inject(this)

        // TODO subscription

        initView()
    }

    private fun initView() {
        /*setSupportActionBar(binding.toolbar)
        var toggle = ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.open, R.string.close)
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        //binding.navView.itemIconTintList(null)
        binding.navView.setCheckedItem(R.id.nav_main_list)
        */
    }

    override fun onBackStackChanged() {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return true
    }

}
