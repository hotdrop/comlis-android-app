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
import io.reactivex.disposables.CompositeDisposable
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentCategoryBinding
import jp.hotdrop.compl.databinding.ItemCategoryBinding
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import org.parceler.Parcels
import javax.inject.Inject

class CategoryFragment: BaseFragment() {

    @Inject
    lateinit var compositeSubscription: CompositeDisposable

    lateinit var adapter: Adapter
    lateinit var helper: ItemTouchHelper
    lateinit var binding: FragmentCategoryBinding

    companion object {
        @JvmStatic val TAG = CategoryFragment::class.java.simpleName!!
        fun newInstance() = CategoryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        adapter = Adapter(context)

        helper = ItemTouchHelper(CategoryItemTouchHelperCallback(adapter))
        helper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.addItemDecoration(helper)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        // TODO categoryでfindAllする
        binding.fabButton.setOnClickListener { v -> /* todo implements to register activity */}

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_CATEGORY_REGISTER || data == null) {
            return
        }

        val refreshMode = data.getIntExtra(ARG_REFRESH_MODE, REFRESH_NONE)
        val category = Parcels.unwrap<Category>(data.getParcelableExtra(TAG)) ?: return

        when (refreshMode) {
            REFRESH_INSERT -> adapter.add(category)
            REFRESH_UPDATE -> adapter.refresh(category)
            REFRESH_DELETE -> adapter.remove(category)
        }
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    inner class Adapter(context: Context)
        : ArrayRecyclerAdapter<Category, BindingHolder<ItemCategoryBinding>>(context) {

        override fun onBindViewHolder(holder: BindingHolder<ItemCategoryBinding>?, position: Int) {
            if(holder == null) {
                return
            }
            val binding = holder.binding
            binding.category = getItem(position)
            binding.iconReorderCategory.setOnTouchListener { view, motionEvent ->
                if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder)
                }
                false
            }

            // TODO クリック時のリスナーを設定する
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCategoryBinding> {
            return BindingHolder(getContext(), parent, R.layout.item_category)
        }

        fun refresh(category: Category) {
            (0..itemCount).forEach { i ->
                val c = getItem(i)
                if (category == c) {
                    c.change(category)
                    notifyItemRemoved(i)
                }
            }
        }

        fun remove(category: Category) {
            (0..itemCount).forEach { i ->
                val c = getItem(i)
                if (category == c) {
                    removeItem(i)
                    notifyItemRemoved(i)
                }
            }
        }

        fun add(category: Category) {
            addItem(category)
            notifyItemInserted(itemCount)
        }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class CategoryItemTouchHelperCallback(val adapter: Adapter): ItemTouchHelper.Callback() {

        /**
         * dragとswipeの動作指定
         * dragの上下のみ許容する。
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
            return adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }

        /**
         * swipe時は何もしない
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            return
        }

        /**
         * 今は長押し時は何もしない
         */
        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    }
}