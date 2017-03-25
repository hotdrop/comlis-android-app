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
import android.widget.Spinner
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.databinding.FragmentCategoryBinding
import jp.hotdrop.compl.databinding.ItemCategoryBinding
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.parts.ColorSpinner
import jp.hotdrop.compl.viewmodel.CategoryViewModel
import javax.inject.Inject

class CategoryFragment : BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapter: Adapter
    private lateinit var helper: ItemTouchHelper

    companion object {
        @JvmStatic val TAG = CategoryFragment::class.java.simpleName!!
        fun newInstance() = CategoryFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)


        loadData()

        return binding.root
    }

    private fun loadData() {
        var disposable = CategoryDao.findAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { categories -> onLoadSuccess(categories) },
                        { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(categories: List<Category>) {
        adapter = Adapter(context)

        if(categories.isNotEmpty()) {
            adapter.addAll(categories.map{ c -> CategoryViewModel(c, context) })
        }
        
        helper = ItemTouchHelper(CategoryItemTouchHelperCallback(adapter))
        binding.recyclerView.addItemDecoration(helper)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        helper.attachToRecyclerView(binding.recyclerView)

        if(adapter.itemCount > 0) {
            goneInitView()
        } else {
            visibleInitView()
        }
        binding.fabButton.setOnClickListener { showRegisterDialog() }
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load companies." + e.message, Toast.LENGTH_LONG).show()
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
        CategoryDao.updateAllOrder(adapter.getModels())
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    private fun visibleInitView() {
        binding.listEmptyView.visibility = View.VISIBLE
    }

    private fun goneInitView() {
        binding.listEmptyView.visibility = View.GONE
    }

    /**
     * ダイアログで、入力した分類名に応じてボタンと注意書きの制御を行う拡張関数
     */
    private val NONE: Int = -1
    fun AppCompatEditText.changeTextListener(view: View, dialog: AlertDialog, editText: AppCompatEditText, categoryId: Int = NONE, originName: String = "") =
            addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/*no op*/}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*no op*/ }
                override fun afterTextChanged(s: Editable?) {
                    val editTxt = editText.text.toString()
                    when (categoryId) {
                        NONE -> if(CategoryDao.exist(editTxt)) disableButton() else enableButton()
                        else -> if(editTxt != originName && CategoryDao.exist(editTxt, categoryId)) disableButton() else enableButton()
                    }
                }
                private fun disableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById(R.id.label_category_attention).visibility = View.VISIBLE
                }
                private fun enableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    view.findViewById(R.id.label_category_attention).visibility = View.GONE
                }
            })

    private fun showRegisterDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_category_register, null)
        val editText = view.findViewById(R.id.text_category_name) as AppCompatEditText
        val spinner = ColorSpinner(view.findViewById(R.id.spinner_color_type) as Spinner, context)
        val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.category_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.category_dialog_add_button, { dialogInterface, _ ->
                    CategoryDao.insert(editText.text.toString(), spinner.getSelection())
                    val vm = CategoryDao.find(editText.text.toString())
                    adapter.add(CategoryViewModel(vm, context))
                    dialogInterface.dismiss()
                    goneInitView()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        editText.changeTextListener(view, dialog, editText)
    }

    private fun showUpdateDialog(vm: CategoryViewModel) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_category_register, null)
        val editText = view.findViewById(R.id.text_category_name) as AppCompatEditText
        editText.setText(vm.viewName as CharSequence)
        val spinner = ColorSpinner(view.findViewById(R.id.spinner_color_type) as Spinner, context)
        spinner.setSelection(vm.category.colorType)
        val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle(R.string.category_dialog_title)
                .setView(view)
                .setPositiveButton(R.string.category_dialog_update_button, { dialogInterface, _ ->
                    vm.viewName = editText.text.toString()
                    vm.category.colorType = spinner.getSelection()
                    CategoryDao.update(vm.makeCategory())
                    adapter.refresh(vm)
                    dialogInterface.dismiss()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        editText.changeTextListener(view, dialog, editText, vm.category.id, vm.viewName)
    }

    /**
     * アダプター
     */
    inner class Adapter(context: Context)
        : ArrayRecyclerAdapter<CategoryViewModel, BindingHolder<ItemCategoryBinding>>(context) {

        override fun onBindViewHolder(holder: BindingHolder<ItemCategoryBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding
            binding.viewModel = getItem(position)
            binding.iconReorderGroup.setOnTouchListener { _, motionEvent ->
                if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder)
                }
                false
            }

            binding.cardView.setOnClickListener { showUpdateDialog(binding.viewModel) }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCategoryBinding> {
            return BindingHolder(context, parent, R.layout.item_category)
        }

        fun refresh(vm: CategoryViewModel) {
            (0..itemCount - 1).forEach { i ->
                val c = getItem(i)
                if (vm == c) {
                    c.change(vm)
                    notifyItemChanged(i)
                }
            }
        }

        fun remove(vm: CategoryViewModel) {
            (0..itemCount - 1).forEach { i ->
                val c = getItem(i)
                if (vm == c) {
                    removeItem(i)
                    notifyItemRemoved(i)
                }
            }
        }

        fun add(vm: CategoryViewModel) {
            addItem(vm)
            notifyItemInserted(itemCount)
        }

        fun getModels(): MutableList<Category> {
            return list.map {vm -> vm.category}.toMutableList()
        }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class CategoryItemTouchHelperCallback(val adapter: Adapter): ItemTouchHelper.Callback() {

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
            return adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        }

        /**
         * swipe時は何もしない
         */
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            return
        }

        /**
         * とりあえず長押し時も何もしない
         */
        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    }
}
