package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentCompanyTabBinding
import jp.hotdrop.compl.databinding.ItemCompanyBinding
import jp.hotdrop.compl.databinding.ItemCompanyListTagBinding
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.viewmodel.CompaniesViewModel
import jp.hotdrop.compl.viewmodel.CompanyViewModel
import javax.inject.Inject

class CompanyTabFragment: BaseFragment() {

    @Inject
    lateinit var viewModel: CompaniesViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private val categoryId by lazy { arguments.getInt(EXTRA_CATEGORY_ID) }

    private lateinit var binding: FragmentCompanyTabBinding
    private lateinit var adapter: Adapter
    private lateinit var helper: ItemTouchHelper
    private var isReordered = false

    companion object {
        private val EXTRA_CATEGORY_ID = "categoryId"
        fun create(categoryId: Int) = CompanyTabFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_CATEGORY_ID, categoryId) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyTabBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        loadData()
        return binding.root
    }

    private fun loadData() {
        viewModel.getData(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess =  { initView(it) },
                        onError = { showErrorAsToast(ErrorType.LoadFailureCompanies, it) }
                )
                .addTo(compositeDisposable)
    }

    private fun initView(companyViewModels: List<CompanyViewModel>) {
        adapter = Adapter(context)

        if(companyViewModels.isNotEmpty()) {
            adapter.addAll(companyViewModels)
            viewModel.goneEmptyMessageOnScreen()
        } else {
            viewModel.visibilityEmptyMessageOnScreen()
        }

        helper = ItemTouchHelper(CompanyItemTouchHelperCallback(adapter))

        binding.recyclerView.let {
            it.addItemDecoration(helper)
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(activity)
        }

        helper.attachToRecyclerView(binding.recyclerView)
    }

    override fun onStop() {
        super.onStop()
        if(isReordered) {
            viewModel.updateItemOrder(adapter.getCompanyIdsAsCurrentOrder())
            isReordered = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != Request.Detail.code || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, NONE)
        val companyId = data.getIntExtra(EXTRA_COMPANY_ID, -1)

        when(refreshMode) {
            UPDATE ->  adapter.refresh(viewModel.getCompanyViewModel(companyId))
            DELETE ->  {
                adapter.remove(companyId)
                if(adapter.itemCount <= 0) {
                    viewModel.visibilityEmptyMessageOnScreen()
                }
            }
            CHANGE_CATEGORY -> activity.intent = data
        }
    }

    fun scrollUpToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    /**
     * アダプター
     * ViewModelでBaseObservableを継承し、ObservableListを使用してCallbackで変更を通知していたが
     * ItemTouchHelperとの連携がうまく行かなかったのでやめた。
     */
    inner class Adapter(context: Context):
            ArrayRecyclerAdapter<CompanyViewModel, BindingHolder<ItemCompanyBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCompanyBinding> {
            return BindingHolder(context, parent, R.layout.item_company)
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemCompanyBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding.apply {
                viewModel = getItem(position)
                iconReorder.setOnTouchListener { _, motionEvent ->
                    if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                        onStartDrag(holder)
                    }
                    false
                }
            }

            binding.cardView.setOnClickListener {
                ActivityNavigator.showCompanyDetail(this@CompanyTabFragment, binding.viewModel.getId(), Request.Detail.code)
            }

            // Recycle後に復帰した際、追加したタグを削除しておかないとタグが積まれていくためremoveAllする。
            binding.flexBoxContainer.removeAllViews()
            binding.viewModel.viewTags.forEach { tag -> setCardView(binding.flexBoxContainer, tag) }
            initFavoriteEvent(binding)
        }

        private fun setCardView(flexboxLayout: FlexboxLayout, tag: Tag) {
            val binding = DataBindingUtil.inflate<ItemCompanyListTagBinding>(getLayoutInflater(null),
                    R.layout.item_company_list_tag, flexboxLayout, false)
            binding.viewModel = viewModel.getTagAssociateViewModel(tag)
            flexboxLayout.addView(binding.root)
        }

        fun refresh(vm: CompanyViewModel) {
            val position = adapter.getItemPosition(vm) ?: return
            adapter.getItem(position).change(vm)
            notifyItemChanged(position)
        }

        fun add(vm: CompanyViewModel) {
            adapter.addItem(vm)
            adapter.notifyItemInserted(adapter.itemCount)
        }

        fun remove(companyId: Int) {
            list.forEachIndexed { index, vm ->
                if(vm.getId() == companyId) {
                    adapter.removeItem(index)
                    notifyItemRemoved(index)
                }
            }
        }

        fun getCompanyIdsAsCurrentOrder(): List<Int> {
            return list.map { vm -> vm.getId() }.toMutableList()
        }

        private fun initFavoriteEvent(binding: ItemCompanyBinding) {

            val vm = binding.viewModel
            binding.animationView1.apply {
                setFavoriteStar()
                setOnClickListener { vm.onClickFirstFavorite(binding) }
            }
            binding.animationView2.apply {
                setFavoriteStar()
                setOnClickListener { vm.onClickSecondFavorite(binding) }
            }
            binding.animationView3.apply {
                setFavoriteStar()
                setOnClickListener { vm.onClickThirdFavorite(binding) }
            }
            vm.playFavorite(binding)
        }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class CompanyItemTouchHelperCallback(val adapter: Adapter): ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFrags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            return makeMovementFlags(dragFrags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            if(viewHolder == null || target == null) {
                return false
            }
            isReordered = true
            adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            return
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    }
}