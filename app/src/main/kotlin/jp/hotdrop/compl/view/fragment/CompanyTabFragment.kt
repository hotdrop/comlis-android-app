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
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
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
    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var jobEvaluationDao: JobEvaluationDao

    private val categoryId by lazy { arguments.getInt(EXTRA_CATEGORY_ID) }

    private lateinit var binding: FragmentCompanyTabBinding
    private lateinit var adapter: Adapter
    private lateinit var helper: ItemTouchHelper

    private var isMoveItem = false

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
        loadData()
        return binding.root
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
        if(isMoveItem) {
            companyDao.updateAllOrder(adapter.getCompanyIdsAsCurrentOrder())
            isMoveItem = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != Request.Detail.code || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, NONE)
        val companyId = data.getIntExtra(EXTRA_COMPANY_ID, -1)
        assert(companyId == -1)

        when(refreshMode) {
            UPDATE -> {
                val vm = CompanyViewModel(companyDao.find(companyId), context, companyDao, categoryDao, jobEvaluationDao)
                adapter.refresh(vm)
            }
            // notifyRemoveで実装した場合、並び替えしてから削除するとConcurrentModificationExceptionになるためリスト再生成する。
            DELETE -> loadData()
            CHANGE_CATEGORY -> activity.intent = data
        }
    }

    private fun loadData() {
        val disposable = companyDao.findByCategory(categoryId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { companies -> onLoadSuccess(companies) },
                        { throwable -> showErrorAsToast(ErrorType.LoadFailureCompanies, throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(companies: List<Company>) {
        adapter = Adapter(context)

        if(companies.isNotEmpty()) {
            adapter.addAll(companies.map{ company -> CompanyViewModel(company, context, companyDao, categoryDao, jobEvaluationDao) })
            goneEmptyMessage()
        } else {
            visibleEmptyMessage()
        }

        helper = ItemTouchHelper(CompanyItemTouchHelperCallback(adapter))

        binding.recyclerView.let {
            it.addItemDecoration(helper)
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(activity)
        }

        helper.attachToRecyclerView(binding.recyclerView)
    }

    private fun visibleEmptyMessage() {
        binding.listEmptyView.visibility = View.VISIBLE
    }

    private fun goneEmptyMessage() {
        binding.listEmptyView.visibility = View.GONE
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
            val binding = holder.binding.apply {
                viewModel = getItem(position)
                iconReorder.setOnTouchListener { _, motionEvent ->
                    if(isMotionEventDown(motionEvent)) onStartDrag(holder)
                    false
                }
            }

            binding.cardView.setOnClickListener {
                ActivityNavigator.showCompanyDetail(this@CompanyTabFragment, binding.viewModel.id, Request.Detail.code)
            }

            // Recycle後に復帰した際、追加したタグを削除しておかないとタグが積まれていくためremoveAllする。
            binding.flexBoxContainer.removeAllViews()
            binding.viewModel.viewTags.forEach { tag -> setCardView(binding.flexBoxContainer, tag) }
            initFavoriteEvent(binding)
        }

        private fun isMotionEventDown(motionEvent: MotionEvent): Boolean {
            return (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN)
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

        fun getCompanyIdsAsCurrentOrder(): List<Int> {
            return list.map { vm -> vm.id }.toMutableList()
        }

        private fun setCardView(flexboxLayout: FlexboxLayout, tag: Tag) {
            val binding = DataBindingUtil.inflate<ItemCompanyListTagBinding>(getLayoutInflater(null),
                    R.layout.item_company_list_tag, flexboxLayout, false)
            binding.viewModel = TagAssociateViewModel(tag = tag, context = context, companyDao = companyDao)
            flexboxLayout.addView(binding.root)
        }

        private fun initFavoriteEvent(binding: ItemCompanyBinding) {

            val vm = binding.viewModel
            val animView1 = binding.animationView1.apply {
                setFavoriteStar()
                setOnClickListener { vm.onClickFirstFavorite(binding) }
            }
            val animView2 = binding.animationView2.apply {
                setFavoriteStar()
                setOnClickListener { vm.onClickSecondFavorite(binding) }
            }
            val animView3 = binding.animationView3.apply {
                setFavoriteStar()
                setOnClickListener { vm.onClickThirdFavorite(binding) }
            }
            mutableListOf(animView1, animView2, animView3).take(vm.viewFavorite).forEach { it.playAnimation() }
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