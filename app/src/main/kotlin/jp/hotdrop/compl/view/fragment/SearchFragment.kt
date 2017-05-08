package jp.hotdrop.compl.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentSearchBinding
import jp.hotdrop.compl.databinding.ItemSearchResultBinding
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.viewmodel.ItemSearchResultViewModel
import jp.hotdrop.compl.viewmodel.SearchViewModel
import javax.inject.Inject

class SearchFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var viewModel: SearchViewModel

    lateinit var binding: FragmentSearchBinding
    private var adapter: Adapter? = null

    private var searchText: String? = null

    companion object {
        fun create() = SearchFragment().apply {  }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val text = searchText ?: return
        search(text)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.menu_search -> return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.search_menu, menu)

        val menuItem = menu?.findItem(R.id.menu_search)
        MenuItemCompat.setOnActionExpandListener(menuItem, object: MenuItemCompat.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                activity?.finish()
                return false
            }
        })

        val searchView = (MenuItemCompat.getActionView(menuItem) as SearchView).apply {
            setIconifiedByDefault(false)
            // searchViewの位置が中央すぎるので、xmlでのgravity調整やtoolbarをいじったり、contentInsetStartWithNavigationしたりした。
            // しかし、どれもこれも色々試してもうんともすんとも言わないのでとりあえずPaddingで対応・・
            setPadding(-52,0,0,0)
            queryHint = context.getString(R.string.hint_search_text_field)
            clearFocus()
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                onQueryTextChange(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText == null || newText.isBlank()) {
                    if(adapter != null) {
                        adapter!!.clearAll()
                    }
                    return true
                }
                search(newText)
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    private fun search(newText: String) {
        searchText = newText
        val disposable = viewModel.getSearchResults(newText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onLoadSuccess(it) },
                        { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(searchResults: List<ItemSearchResultViewModel>) {
        adapter = Adapter(context)
        if(searchResults.isNotEmpty()) {
            adapter!!.addAll(searchResults)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed search." + e.message, Toast.LENGTH_LONG).show()
    }

    inner class Adapter(context: Context)
        : ArrayRecyclerAdapter<ItemSearchResultViewModel, BindingHolder<ItemSearchResultBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemSearchResultBinding> {
            return BindingHolder(context, parent, R.layout.item_search_result)
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemSearchResultBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding
            binding.viewModel = getItem(position)
            binding.cardView.setOnClickListener {
                ActivityNavigator.showCompanyDetail(this@SearchFragment, binding.viewModel.id, REQ_CODE_COMPANY_DETAIL)
            }
            val animView1 = binding.animationView1.setFavoriteStar()
            val animView2 = binding.animationView2.setFavoriteStar()
            val animView3 = binding.animationView3.setFavoriteStar()
            mutableListOf(animView1, animView2, animView3)
                    .take(binding.viewModel.favorite)
                    .forEach { it.progress = 1.toFloat() }
        }

        fun clearAll() {
            clear()
            notifyDataSetChanged()
        }
    }
}