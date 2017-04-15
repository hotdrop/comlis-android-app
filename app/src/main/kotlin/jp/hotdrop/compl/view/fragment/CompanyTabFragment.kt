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
import android.widget.Toast
import com.airbnb.lottie.LottieAnimationView
import com.google.android.flexbox.FlexboxLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.databinding.FragmentCompanyTabBinding
import jp.hotdrop.compl.databinding.ItemCompanyBinding
import jp.hotdrop.compl.databinding.ItemCompanyListTagBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.viewmodel.CompanyViewModel
import jp.hotdrop.compl.viewmodel.TagAssociateViewModel
import javax.inject.Inject

class CompanyTabFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private val categoryId by lazy {
        arguments.getInt(EXTRA_CATEGORY_ID)
    }

    private lateinit var binding: FragmentCompanyTabBinding
    private lateinit var adapter: Adapter
    private lateinit var helper: ItemTouchHelper

    private var isMoveItem = false

    companion object {
        @JvmStatic private val EXTRA_CATEGORY_ID = "categoryId"
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
        loadData()
        return binding.root
    }

    private fun loadData() {
        val disposable = CompanyDao.findByCategory(categoryId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { companies -> onLoadSuccess(companies) },
                        { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(companies: List<Company>) {
        adapter = Adapter(context)

        if(companies.isNotEmpty()) {
            adapter.addAll(companies.map{ company -> CompanyViewModel(company, context) })
            goneEmptyMessage()
        } else {
            visibleEmptyMessage()
        }

        helper = ItemTouchHelper(CompanyItemTouchHelperCallback(adapter))
        binding.recyclerView.addItemDecoration(helper)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        helper.attachToRecyclerView(binding.recyclerView)
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load companies." + e.message, Toast.LENGTH_LONG).show()
    }

    private fun visibleEmptyMessage() {
        binding.listEmptyView.visibility = View.VISIBLE
    }

    private fun goneEmptyMessage() {
        binding.listEmptyView.visibility = View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_COMPANY_DETAIL || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, NONE)
        val companyId = data.getIntExtra(EXTRA_COMPANY_ID, -1)
        assert(companyId == -1)

        when(refreshMode) {
            UPDATE -> {
                val vm = CompanyViewModel(CompanyDao.find(companyId), context)
                adapter.refresh(vm)
            }
            // notifyRemoveで実装した場合、並び替えしてから削除するとConcurrentModificationExceptionになるためリスト再生成する。
            DELETE -> loadData()
            CHANGE_CATEGORY -> activity.intent = data
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
        if(isMoveItem) {
            CompanyDao.updateAllOrder(adapter.getModels())
            isMoveItem = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    fun scrollUpToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    inner class Adapter(context: Context):
            ArrayRecyclerAdapter<CompanyViewModel, BindingHolder<ItemCompanyBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCompanyBinding> {
            return BindingHolder(context, parent, R.layout.item_company)
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemCompanyBinding>?, position: Int) {
            holder ?: return
            // applyにしようと思ったがネストが深くなるのでこのままにした。
            val binding = holder.binding
            binding.viewModel = getItem(position)
            binding.iconReorder.setOnTouchListener { _, motionEvent ->
                if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder)
                }
                false
            }

            binding.cardView.setOnClickListener {
                ActivityNavigator.showCompanyDetail(this@CompanyTabFragment, binding.viewModel.company.id, REQ_CODE_COMPANY_DETAIL)
            }

            binding.viewModel.viewTags.forEach { tag -> setCardView(binding.flexBoxContainer, tag) }
            initAnimationView(binding)
        }

        fun refresh(vm: CompanyViewModel) {
            val position = adapter.getItemPosition(vm)
            if(position != -1) {
                adapter.getItem(position).change(vm)
                notifyItemChanged(position)
            }
        }

        fun add(vm: CompanyViewModel) {
            adapter.addItem(vm)
            adapter.notifyItemInserted(adapter.itemCount)
        }

        fun getModels(): List<Company> {
            return list.map { vm -> vm.company }.toMutableList()
        }

        private fun setCardView(view: FlexboxLayout, tag: Tag) {
            val binding = DataBindingUtil.inflate<ItemCompanyListTagBinding>(getLayoutInflater(null),
                    R.layout.item_company_list_tag, view, false)
            binding.viewModel = TagAssociateViewModel(tag = tag, context = context)
            view.addView(binding.root)
        }

        private val RESET = 0.toFloat()
        private fun initAnimationView(binding: ItemCompanyBinding) {
            // 弱い参照でいいと思うのでこれにする
            val animView1 = binding.animationView1.apply { setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak) }
            val animView2 = binding.animationView2.apply { setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak) }
            val animView3 = binding.animationView3.apply { setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak) }
            val animViews = mutableListOf(animView1, animView2, animView3)

            val vm = binding.viewModel
            animViews.take(vm.viewFavorite).forEach { it.playAnimation() }

            // ここの書き方が単調すぎるのでなんとかならないものか・・
            animView1.setOnClickListener {
                if(vm.isOneFavorite()) {
                    animView1.progress = RESET
                    vm.resetFavorite()
                } else {
                    animView1.playAnimation()
                    animView2.progress = RESET
                    animView3.progress = RESET
                    vm.tapFavorite(1)
                }
            }
            animView2.setOnClickListener {
                if(vm.isTwoFavorite()) {
                    animView1.progress = RESET
                    animView2.progress = RESET
                    vm.resetFavorite()
                } else {
                    animView1.playAnimation()
                    animView2.playAnimation()
                    animView3.progress = RESET
                    vm.tapFavorite(2)
                }
            }
            animView3.setOnClickListener {
                if(vm.isThreeFavorite()) {
                    animViews.forEach { it.progress = RESET }
                    vm.resetFavorite()
                } else {
                    animViews.forEach { it.playAnimation() }
                    vm.tapFavorite(3)
                }
            }
        }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class CompanyItemTouchHelperCallback(val adapter: CompanyTabFragment.Adapter): ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFrags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            return makeMovementFlags(dragFrags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            if(viewHolder == null || target == null) {
                return false
            }
            isMoveItem = true
            return adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            return
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    }
}