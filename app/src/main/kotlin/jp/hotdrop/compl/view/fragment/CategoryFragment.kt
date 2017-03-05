package jp.hotdrop.compl.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.databinding.FragmentCategoryBinding
import jp.hotdrop.compl.databinding.ItemCategoryBinding
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder

class CategoryFragment : BaseFragment() {

    private val adapter by lazy {
        Adapter(context)
    }

    private val helper by lazy {
        ItemTouchHelper(CategoryItemTouchHelperCallback(adapter))
    }

    lateinit var binding: FragmentCategoryBinding

    companion object {
        @JvmStatic val TAG = CategoryFragment::class.java.simpleName!!
        fun newInstance() = CategoryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        binding.recyclerView.addItemDecoration(helper)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        helper.attachToRecyclerView(binding.recyclerView)

        adapter.addAll(CategoryDao.findAll())

        // TODO これだと最初の１回目はずっと表示され続けてしまうので考える。
        binding.listEmptyView.visibility = if(adapter.itemCount > 0) View.GONE else View.VISIBLE
        binding.fabButton.setOnClickListener { showGroupRegisterDialog() }
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // TODO 並び順の更新を行う
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    /**
     * ダイアログで、入力した分類名に応じてボタンと注意書きの制御を行う拡張関数
     */
    private val NONE: Int = -1
    fun AppCompatEditText.changeTextListener(view: View, dialog: AlertDialog, editText: AppCompatEditText, categoryId: Int = NONE) =
            addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/*no op*/}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*no op*/ }
                override fun afterTextChanged(s: Editable?) {
                    val editTxt = editText.text.toString()
                    when (categoryId) {
                        NONE -> if(CategoryDao.exist(editTxt)) duplicateGroupName() else allRightGroupName()
                        else ->  if(CategoryDao.exist(editTxt, categoryId)) duplicateGroupName() else allRightGroupName()
                    }
                }
                private fun duplicateGroupName() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById(R.id.label_group_attention).visibility = View.VISIBLE
                }
                private fun allRightGroupName() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    view.findViewById(R.id.label_group_attention).visibility = View.GONE
                }
            })

    private fun showGroupRegisterDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_group_register, null)
        val editText = view.findViewById(R.id.text_group_name) as AppCompatEditText
        val dialog = AlertDialog.Builder(activity, R.style.DialogTheme)
                .setTitle(R.string.group_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.group_dialog_add_button, { dialogInterface, _ ->
                    CategoryDao.insert(editText.text.toString())
                    val group = CategoryDao.find(editText.text.toString())
                    adapter.add(group)
                    dialogInterface.dismiss()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        editText.changeTextListener(view, dialog, editText)
    }

    private fun showUpdateDialog(category: Category) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_group_register, null)
        val editText = view.findViewById(R.id.text_group_name) as AppCompatEditText
        editText.setText(category.name as CharSequence)
        val dialog = AlertDialog.Builder(activity, R.style.DialogTheme)
                .setTitle(R.string.group_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.group_dialog_update_button, { dialogInterface, _ ->
                    category.name = editText.text.toString()
                    CategoryDao.update(category)
                    adapter.refresh(category)
                    dialogInterface.dismiss()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        editText.changeTextListener(view, dialog, editText, category.id)
    }

    /**
     * アダプター
     */
    inner class Adapter(context: Context)
        : ArrayRecyclerAdapter<Category, BindingHolder<ItemCategoryBinding>>(context) {

        override fun onBindViewHolder(holder: BindingHolder<ItemCategoryBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding
            binding.category = getItem(position)
            binding.iconReorderGroup.setOnTouchListener { _, motionEvent ->
                if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder)
                }
                false
            }

            binding.cardView.setOnClickListener { showUpdateDialog(binding.category) }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCategoryBinding> {
            return BindingHolder(context, parent, R.layout.item_category)
        }

        fun refresh(group: Category) {
            (0..itemCount).forEach { i ->
                val c = getItem(i)
                if (group == c) {
                    c.change(group)
                    notifyItemRemoved(i)
                }
            }
        }

        fun remove(group: Category) {
            (0..itemCount).forEach { i ->
                val c = getItem(i)
                if (group == c) {
                    removeItem(i)
                    notifyItemRemoved(i)
                }
            }
        }

        fun add(group: Category) {
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
         * TODO ここはdragの上下のみ許容するようになっているのでswipeも指定する
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
         * swipe時は削除。ただしアイテムが１個以上登録されている場合は削除できないものとする。
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
