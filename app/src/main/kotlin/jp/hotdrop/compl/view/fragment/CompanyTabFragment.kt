package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.databinding.FragmentCompanyTabBinding
import jp.hotdrop.compl.databinding.ItemCompanyBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.viewmodel.CompanyViewModel
import javax.inject.Inject

class CompanyTabFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    private var categoryId: Int = 0

    private lateinit var binding: FragmentCompanyTabBinding
    private lateinit var adapter: Adapter
    private lateinit var helper: ItemTouchHelper

    private var isMoveItem = false

    companion object {
        @JvmStatic val EXTRA_CATEGORY_ID = "categoryId"
        fun create(categoryId: Int) = CompanyTabFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_CATEGORY_ID, categoryId) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        categoryId = arguments.getInt(EXTRA_CATEGORY_ID)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyTabBinding.inflate(inflater, container, false)
        adapter = Adapter(context)
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
        if(companies.isNotEmpty()) {
            adapter.addAll(companies.map{ company -> CompanyViewModel(company, context) })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_COMPANY_DETAIL || data == null) {
            return
        }
        activity.intent = data
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

            initAnimationView(binding)
        }

        fun add(vm: CompanyViewModel) {
            adapter.addItem(vm)
            adapter.notifyItemInserted(adapter.itemCount)
        }

        fun getModels(): MutableList<Company> {
            return list.map { vm -> vm.company }.toMutableList()
        }

        private val RESET = 0.toFloat()
        private fun initAnimationView(binding: ItemCompanyBinding) {
            // ロード時にキャッシュして問題ないのでStrongにした。コード読む限り最初の1つだけでいいと思う。
            val animView1 = binding.animationView1.apply { setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Strong) }
            val animView2 = binding.animationView2.apply { setAnimation("FavoriteStar.json") }
            val animView3 = binding.animationView3.apply { setAnimation("FavoriteStar.json") }
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

        /**
         * dragとswipeの動作指定。今はdragのみ
         */
        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFrags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            return makeMovementFlags(dragFrags, swipeFlags)
        }

        /**
         * drag時の動作
         */
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            if(viewHolder == null || target == null) {
                return false
            }
            isMoveItem = true
            return adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }

        /**
         * swipe時はとりあえず何もしない
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            return
        }

        /**
         * 長押し時は何もしない
         */
        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    }
}