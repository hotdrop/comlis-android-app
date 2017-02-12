package jp.hotdrop.compl.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityMainBinding
import jp.hotdrop.compl.fragment.CompanyFragment

class MainActivity : BaseActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        FragmentManager.OnBackStackChangedListener{

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // ヘッダー情報を取得しバインドへ設定
        DataBindingUtil.bind<ActivityMainBinding>(binding.navView.getHeaderView(0))
        getComponent().inject(this)

        initView()

        if(savedInstanceState == null) {
            replaceFragment(CompanyFragment.newInstance())
        }

        supportFragmentManager.addOnBackStackChangedListener(this)
    }

    private fun initView() {
        // スライド式のメニューを生成する
        setSupportActionBar(binding.toolbar)
        var toggle = ActionBarDrawerToggle(this, binding.drawer, binding.toolbar, R.string.open, R.string.close)
        binding.drawer.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        binding.navView.itemIconTintList = null
        binding.navView.setCheckedItem(R.id.nav_main_list)
        // TODO 今はなぜか隠れないので下の処理を入れているが、本来は不要のはず
        binding.navView.visibility = View.GONE
    }

    private fun replaceFragment(fragment: Fragment) {
        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        // TODO simpleNameは各Fragmentに持つ
        ft.replace(R.id.content_view, fragment, fragment.javaClass.simpleName)
        ft.addToBackStack(null)
        ft.commit()
    }

    override fun onBackStackChanged() {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
