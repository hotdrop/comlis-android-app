package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.databinding.ObservableList
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
import io.reactivex.disposables.CompositeDisposable
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

class CompanyTabFragment: BaseFragment(), CompaniesViewModel.Callback {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var viewModel: CompaniesViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyTabBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        viewModel.loadData(categoryId)
        initView()
        return binding.root
    }

    private fun initView() {
        adapter = Adapter(context, viewModel.getViewModels())
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
            viewModel.updateItemOrder()
            isReordered = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }

    override fun showError(throwable: Throwable) {
        showErrorAsToast(ErrorType.LoadFailureCompanies, throwable)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != Request.Detail.code || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, NONE)
        val companyId = data.getIntExtra(EXTRA_COMPANY_ID, -1)

        when(refreshMode) {
            UPDATE -> viewModel.update(companyId)
            DELETE -> viewModel.delete(companyId)
            CHANGE_CATEGORY -> activity.intent = data
        }
    }

    fun scrollUpToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    inner class Adapter(context: Context, list: ObservableList<CompanyViewModel>):
            ArrayRecyclerAdapter<CompanyViewModel, BindingHolder<ItemCompanyBinding>>(context, list) {

        init {
            list.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<CompanyViewModel>>() {
                override fun onChanged(sender: ObservableList<CompanyViewModel>?) {
                    notifyDataSetChanged()
                }
                override fun onItemRangeInserted(sender: ObservableList<CompanyViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeInserted(positionStart, itemCount)
                }
                override fun onItemRangeChanged(sender: ObservableList<CompanyViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeChanged(positionStart, itemCount)
                }
                override fun onItemRangeRemoved(sender: ObservableList<CompanyViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeRemoved(positionStart, itemCount)
                }
                override fun onItemRangeMoved(sender: ObservableList<CompanyViewModel>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }
            })
        }

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
                ActivityNavigator.showCompanyDetail(this@CompanyTabFragment, binding.viewModel.getId(), Request.Detail.code)
            }

            // Recycle後に復帰した際、追加したタグを削除しておかないとタグが積まれていくためremoveAllする。
            binding.flexBoxContainer.removeAllViews()
            binding.viewModel.viewTags.forEach { tag -> setCardView(binding.flexBoxContainer, tag) }
            initFavoriteEvent(binding)
        }

        private fun isMotionEventDown(motionEvent: MotionEvent): Boolean {
            return (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN)
        }

        private fun setCardView(flexboxLayout: FlexboxLayout, tag: Tag) {
            val binding = DataBindingUtil.inflate<ItemCompanyListTagBinding>(getLayoutInflater(null),
                    R.layout.item_company_list_tag, flexboxLayout, false)
            binding.viewModel = viewModel.getTagAssociateViewModel(tag)
            flexboxLayout.addView(binding.root)
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
     *
     * 最初はObservableListを使わず、自力でadapter.getItemPositionでpositionを取得しnotifyItemChangedなどを実装していた。
     * しかし、変更を逐次CallbackしてくれるObservableListでRecyclerViewの変更を制御した方が良い感じに実装できそうだったため、
     * ViewModelでBaseObservableを継承してadapterでaddOnListChangedCallbackを実装して対応した。
     *
     * この状態（AdapterとViewModelだけ修正してItemTouchHelperCallbackはそのままの状態）で、アイテムの並び順をタッチイベントで
     * 変えようとするとモーションのブレ、残像、アイテムの分身などが発生する。
     *
     * 原因は、最初のItemTouchHelperCallbackの実装のせい。
     * 最初の段階ではタッチイベントでアイテムを動かした瞬間にアイテムを保持しているコレクションとnotifyItemMovedイベントを発生させていた。
     * 上下の移動でかつゴリゴリ変更結果をadapterに書いていた時はこの実装でよかったのだが、ObservableListを使って変更内容をCallbackする
     * ようにしたのでアイテムを動かしている間も変更内容がコールバックされてしまい色々おかしくなった。
     *
     * 従って、コールバックでの変更通知（実際にはArrayRecyclerAdapterが保持するアイテムリスト中のアイテム入れ替えタイミング）は
     * UI上でのアイテム移動が完了したタイミングで行うようにした。
     */
    inner class CompanyItemTouchHelperCallback(val adapter: Adapter): ItemTouchHelper.Callback() {

        val NONE_POSITION = -1
        var fromPosition = NONE_POSITION
        var toPosition = NONE_POSITION

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFrags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            return makeMovementFlags(dragFrags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            if(viewHolder == null || target == null) {
                return false
            }
            if(fromPosition == NONE_POSITION) {
                fromPosition = viewHolder.adapterPosition
            }
            toPosition = target.adapterPosition
            adapter.onNotifyItemMoved(viewHolder.adapterPosition, target.adapterPosition)
            isReordered = true
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            return
        }

        override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
            super.clearView(recyclerView, viewHolder)
            if(fromPosition != NONE_POSITION && toPosition != NONE_POSITION && fromPosition != toPosition) {
                adapter.onItemMove(fromPosition, toPosition)
            }
            fromPosition = NONE_POSITION
            toPosition = NONE_POSITION
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    }
}