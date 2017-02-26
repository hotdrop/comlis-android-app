package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.GroupDao
import jp.hotdrop.compl.databinding.FragmentGroupBinding
import jp.hotdrop.compl.databinding.ItemGroupBinding
import jp.hotdrop.compl.model.Group
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import org.parceler.Parcels
import javax.inject.Inject

class GroupFragment : BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    lateinit var adapter: Adapter
    lateinit var helper: ItemTouchHelper
    lateinit var binding: FragmentGroupBinding

    companion object {
        @JvmStatic val TAG = GroupFragment::class.java.simpleName!!
        fun newInstance() = GroupFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentGroupBinding.inflate(inflater, container, false)
        adapter = Adapter(context)

        helper = ItemTouchHelper(CategoryItemTouchHelperCallback(adapter))
        helper.attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.addItemDecoration(helper)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        loadData()

        binding.fabButton.setOnClickListener { v ->
            //ActivityNavigator.showGroupRegister(this, REQ_CODE_GROUP_REGISTER)
            showGroupRegisterDialog()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_GROUP_REGISTER || data == null) {
            return
        }

        val refreshMode = data.getIntExtra(REFRESH_MODE, REFRESH_NONE)
        val group = Parcels.unwrap<Group>(data.getParcelableExtra(TAG)) ?: return

        when (refreshMode) {
            REFRESH_INSERT -> adapter.add(group)
            REFRESH_UPDATE -> adapter.refresh(group)
            REFRESH_DELETE -> adapter.remove(group)
        }
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        // TODO 並び順の更新を行う
        compositeDisposable.dispose()
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    private fun loadData() {
        val disposable = GroupDao.findAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {list -> onLoadSuccess(list) },
                        {throwable -> onLoadFailure(throwable) })
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(groups: List<Group>) {
        adapter.addAll(groups)
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load companies." + e.message, Toast.LENGTH_LONG).show()
    }

    private fun showGroupRegisterDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_group_register, null)
        // TODO ダイアログのところから
        AlertDialog.Builder(activity, R.style.DialogTheme)
                .setTitle(R.string.group_dialog_title)
                .setView(view)
                .setPositiveButton("登録する", { dialogInterface, i ->
                    // TODO 同名チェックする。
                    val editText = view.findViewById(R.id.text_group_name) as AppCompatEditText
                    Toast.makeText(activity, editText.text, Toast.LENGTH_LONG).show()
                    dialogInterface.dismiss()
                })
                .show()
    }

    inner class Adapter(context: Context)
        : ArrayRecyclerAdapter<Group, BindingHolder<ItemGroupBinding>>(context) {

        override fun onBindViewHolder(holder: BindingHolder<ItemGroupBinding>?, position: Int) {
            if(holder == null) {
                return
            }
            val binding = holder.binding
            binding.group = getItem(position)
            binding.iconReorderGroup.setOnTouchListener { view, motionEvent ->
                if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder)
                }
                false
            }

            // TODO クリック時のリスナーを設定する
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemGroupBinding> {
            return BindingHolder(context, parent, R.layout.item_group)
        }

        fun refresh(group: Group) {
            (0..itemCount).forEach { i ->
                val c = getItem(i)
                if (group == c) {
                    c.change(group)
                    notifyItemRemoved(i)
                }
            }
        }

        fun remove(group: Group) {
            (0..itemCount).forEach { i ->
                val c = getItem(i)
                if (group == c) {
                    removeItem(i)
                    notifyItemRemoved(i)
                }
            }
        }

        fun add(group: Group) {
            addItem(group)
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