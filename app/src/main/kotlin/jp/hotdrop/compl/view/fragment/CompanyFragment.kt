package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toSingle
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.databinding.FragmentCompanyBinding
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.view.StackedPageListener
import jp.hotdrop.compl.view.activity.ActivityNavigator
import javax.inject.Inject


class CompanyFragment: BaseFragment(), StackedPageListener {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var categoryDao: CategoryDao

    private lateinit var binding: FragmentCompanyBinding
    private lateinit var adapter: Adapter
    private var tabName: String? = null

    companion object {
        val TAG: String = CompanyFragment::class.java.simpleName
        fun newInstance() = CompanyFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        tabName = null
        loadData()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val refreshMode = activity.intent.getIntExtra(REFRESH_MODE, NONE)
        if(refreshMode != CHANGE_CATEGORY) {
            return
        }

        // CompanyDetailFragmentを経由して分類変更がされた場合はこのルートを通る
        tabName = activity.intent.getStringExtra(EXTRA_CATEGORY_NAME)
        loadData()
        activity.intent.let {
            it.removeExtra(REFRESH_MODE)
            it.removeExtra(EXTRA_CATEGORY_NAME)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != Request.Register.code || data == null) {
            return
        }
        tabName = data.getStringExtra(EXTRA_CATEGORY_NAME)
        loadData()
    }

    private fun loadData() {
        showProgress()
        categoryDao.findAll()
                .toSingle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { initView(it) },
                        onError = {
                            showErrorAsToast(ErrorType.LoadFailureCompany, it)
                            hideProgress()
                        }
                )
                .addTo(compositeDisposable)
    }

    private fun initView(categories: List<Category>) {
        // FragmentをネストするのでFragmentManagerではなくchildFragmentManagerを使う
        adapter = Adapter(childFragmentManager)
        if(categories.isNotEmpty()) {
            categories.forEach { category -> addFragment(category.name, category.id) }
            binding.listEmptyView.visibility = View.GONE
        } else {
            binding.listEmptyView.visibility = View.VISIBLE
        }

        // tabLayoutを再作成するとonTabSelectedが呼ばれてしまうためこのタイミングで保持しておく。
        val stockSelectedTabName = tabName

        binding.viewPager.adapter = adapter
        binding.tabLayout.run {
            setupWithViewPager(binding.viewPager)
            addOnTabSelectedListener(SelectedTabListener(binding.viewPager))
        }
        binding.fab.setOnClickListener { ActivityNavigator.showCompanyRegister(this, tabName, Request.Register.code) }

        if(stockSelectedTabName != null) {
            binding.viewPager.currentItem = adapter.getPagePosition(stockSelectedTabName)
        }

        hideProgress()
    }

    private fun showProgress() {
        binding.progressBarContainer.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        binding.progressBarContainer.visibility = View.GONE
    }

    private fun addFragment(title: String, categoryId: Int) {
        adapter.add(title, CompanyTabFragment.create(categoryId))
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_company, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // YAGNIに反するが、OptionItemが増えた場合に備えてwhenで実装しておく
        when(item?.itemId) {
            R.id.item_search -> ActivityNavigator.showSearch(this@CompanyFragment)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onTop() {
        loadData()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private inner class Adapter(fm: FragmentManager): FragmentStatePagerAdapter(fm) {

        private val fragments = mutableListOf<CompanyTabFragment>()
        private val titles = mutableListOf<String>()

        override fun getItem(position: Int): Fragment? {
            if(position >= 0 && position < fragments.size) {
                return fragments[position]
            }
            return null
        }

        override fun getCount(): Int {
            return fragments.size
        }

        override fun getPageTitle(position: Int): CharSequence {
            return titles[position]
        }

        fun add(title: String, fragment: CompanyTabFragment) {
            fragments.add(fragment)
            titles.add(title)
            notifyDataSetChanged()
        }

        fun getPagePosition(title: String?): Int {
            title ?: return 0
            return titles.takeWhile { it != title }.count()
        }
    }

    private inner class SelectedTabListener(viewPager: ViewPager): TabLayout.ViewPagerOnTabSelectedListener(viewPager) {

        override fun onTabSelected(tab: TabLayout.Tab?) {
            super.onTabSelected(tab)
            tab ?: return
            tabName = adapter.getPageTitle(tab.position).toString()
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
            super.onTabReselected(tab)
            tab ?: return
            (adapter.getItem(tab.position) as CompanyTabFragment).scrollUpToTop()
        }
    }
}
