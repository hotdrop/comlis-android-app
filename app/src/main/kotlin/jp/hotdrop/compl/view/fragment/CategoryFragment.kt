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
import jp.hotdrop.compl.dao.CompanyDao
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

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapter: Adapter
    private lateinit var helper: ItemTouchHelper
    private var isReorder = false

    companion object {
        @JvmStatic val TAG: String = CategoryFragment::class.java.simpleName
        fun newInstance() = CategoryFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        loadData()
        return binding.root
    }

    private fun loadData() {
        val disposable = categoryDao.findAll()
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
            adapter.addAll(categories.map{ c -> CategoryViewModel(c, context, companyDao) })
            goneEmptyMessage()
        } else {
            visibleEmptyMessage()
        }
        
        helper = ItemTouchHelper(CategoryItemTouchHelperCallback(adapter))
        binding.recyclerView.addItemDecoration(helper)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        helper.attachToRecyclerView(binding.recyclerView)

        binding.fabButton.setOnClickListener { showRegisterDialog() }
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load categories." + e.message, Toast.LENGTH_LONG).show()
    }

    private fun visibleEmptyMessage() {
        binding.listEmptyView.visibility = View.VISIBLE
    }

    private fun goneEmptyMessage() {
        binding.listEmptyView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
        if(isReorder) {
            categoryDao.updateAllOrder(adapter.getModels())
        }
    }

    fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    /**
     * ダイアログで、入力した分類名に応じてボタンと注意書きの制御を行う拡張関数
     */
    private val REGISTER_MODE: Int = -1
    fun AppCompatEditText.changeTextListener(view: View, dialog: AlertDialog, editText: AppCompatEditText,
                                             categoryId: Int = REGISTER_MODE, originName: String = "") =
            addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/*no op*/}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*no op*/ }
                override fun afterTextChanged(s: Editable?) {
                    val editTxt = editText.text.toString()
                    when {
                        editTxt == "" -> disableButton()
                        categoryId == REGISTER_MODE -> {
                            if(categoryDao.exist(editTxt)) {
                                disableButtonWithAttention()
                            } else {
                                enableButton()
                            }
                        }
                        else -> {
                            if(editTxt != originName && categoryDao.exist(editTxt, categoryId)) {
                                disableButtonWithAttention()
                            } else {
                                enableButton()
                            }
                        }
                    }
                }
                private fun disableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById(R.id.label_category_attention).visibility = View.GONE
                }
                private fun disableButtonWithAttention() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById(R.id.label_category_attention).visibility = View.VISIBLE
                }
                private fun enableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    view.findViewById(R.id.label_category_attention).visibility = View.GONE
                }
            })

    private fun showRegisterDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_category, null)
        val editText = view.findViewById(R.id.text_category_name) as AppCompatEditText
        val spinner = ColorSpinner(view.findViewById(R.id.spinner_color_type) as Spinner, context)
        val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                .setView(view)
                .setPositiveButton(R.string.dialog_add_button, { dialogInterface, _ ->
                    categoryDao.insert(editText.text.toString(), spinner.getSelection())
                    val category = categoryDao.find(editText.text.toString())
                    adapter.add(CategoryViewModel(category, context, companyDao))
                    dialogInterface.dismiss()
                    goneEmptyMessage()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        editText.changeTextListener(view, dialog, editText)
    }

    private fun showUpdateDialog(vm: CategoryViewModel) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_category, null)
        val editText = view.findViewById(R.id.text_category_name) as AppCompatEditText
        editText.setText(vm.viewName as CharSequence)
        val spinner = ColorSpinner(view.findViewById(R.id.spinner_color_type) as Spinner, context)
        spinner.setSelection(vm.category.colorType)
        val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                .setView(view)
                .setPositiveButton(R.string.dialog_update_button, { dialogInterface, _ ->
                    vm.viewName = editText.text.toString()
                    vm.category.colorType = spinner.getSelection()
                    categoryDao.update(vm.makeCategory())
                    adapter.refresh(vm)
                    dialogInterface.dismiss()
                })
                .setNegativeButton(R.string.dialog_delete_button, { dialogInterface, _ ->
                    categoryDao.delete(vm.category)
                    adapter.remove(vm)
                    if(adapter.itemCount == 0) {
                        visibleEmptyMessage()
                    }
                    dialogInterface.dismiss()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        if(vm.itemCount.toInt() > 0) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
            view.findViewById(R.id.label_category_delete_attention).visibility = View.VISIBLE
        }
        editText.changeTextListener(view, dialog, editText, vm.category.id, vm.viewName)
    }

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
            val position = adapter.getItemPosition(vm)
            if(position != -1) {
                adapter.getItem(position).change(vm)
                notifyItemChanged(position)
            }
        }

        fun add(vm: CategoryViewModel) {
            addItem(vm)
            notifyItemInserted(itemCount)
        }

        fun remove(vm: CategoryViewModel) {
            val position = adapter.getItemPosition(vm)
            if(position != -1) {
                adapter.removeItem(position)
                notifyItemRemoved(position)
            }
        }

        fun getModels(): List<Category> {
            return list.map {vm -> vm.category}.toMutableList()
        }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class CategoryItemTouchHelperCallback(val adapter: Adapter): ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFrags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            return makeMovementFlags(dragFrags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            if(viewHolder == null || target == null) {
                return false
            }
            isReorder = true
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
