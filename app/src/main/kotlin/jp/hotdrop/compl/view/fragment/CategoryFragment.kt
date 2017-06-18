package jp.hotdrop.compl.view.fragment

import android.content.Context
import android.databinding.ObservableList
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
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentCategoryBinding
import jp.hotdrop.compl.databinding.ItemCategoryBinding
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.parts.ColorSpinner
import jp.hotdrop.compl.viewmodel.CategoriesViewModel
import jp.hotdrop.compl.viewmodel.CategoryViewModel
import javax.inject.Inject

class CategoryFragment : BaseFragment(), CategoriesViewModel.Callback {

    @Inject
    lateinit var viewModel: CategoriesViewModel

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var adapter: Adapter
    private lateinit var helper: ItemTouchHelper
    private var isReorder = false

    companion object {
        val TAG: String = CategoryFragment::class.java.simpleName
        fun newInstance() = CategoryFragment()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setCallback(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        viewModel.loadData()
        initView()
        return binding.root
    }

    private fun initView() {
        adapter = Adapter(context, viewModel.getViewModels())
        helper = ItemTouchHelper(CategoryItemTouchHelperCallback(adapter))

        binding.recyclerView.let {
            it.addItemDecoration(helper)
            it.setHasFixedSize(true)
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(activity)
        }

        helper.attachToRecyclerView(binding.recyclerView)
        binding.fabButton.setOnClickListener { showRegisterDialog() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if(isReorder) {
            viewModel.updateItemOrder()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }

    override fun showError(throwable: Throwable) {
        showErrorAsToast(ErrorType.LoadFailureCategory, throwable)
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
                    when {
                        editText.text.toString() == "" -> disableButton()
                        categoryId == REGISTER_MODE -> if(existName()) disableButtonWithAttention() else enableButton()
                        else -> if(existNameExclusionOwn()) disableButtonWithAttention() else enableButton()
                    }
                }
                private fun existName(): Boolean = viewModel.existName(editText.text.toString())
                private fun existNameExclusionOwn(): Boolean =
                        (editText.text.toString() != originName && viewModel.existNameExclusionId(editText.text.toString(), categoryId))

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
                    viewModel.register(editText.text.toString(), spinner.getSelection())
                    dialogInterface.dismiss()
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
        spinner.setSelection(vm.getColorType())
        val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                .setView(view)
                .setPositiveButton(R.string.dialog_update_button, { dialogInterface, _ ->
                    viewModel.update(vm, editText.text.toString(), spinner.getSelection())
                    dialogInterface.dismiss()
                })
                .setNegativeButton(R.string.dialog_delete_button, { dialogInterface, _ ->
                    viewModel.delete(vm)
                    dialogInterface.dismiss()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        if(vm.isRegisterCompanyInCategory()) {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).isEnabled = false
            view.findViewById(R.id.label_category_delete_attention).visibility = View.VISIBLE
        }
        editText.changeTextListener(view, dialog, editText, vm.getId(), vm.viewName)
    }

    /**
     * アダプター
     */
    inner class Adapter(context: Context, list: ObservableList<CategoryViewModel>)
        :            ArrayRecyclerAdapter<CategoryViewModel, BindingHolder<ItemCategoryBinding>>(context, list) {

        init {
            // ObservableListでのCallbackを行うと並び順を変更する（CardViewのReorderをする）際にモーションのブレが発生する。
            // 特に先頭のCardViewを移動しようとした場合の残像がひどい。あと若干重くなる。
            // 軽い方がいいかもしれないので元の状態に戻すかも・・
            list.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<CategoryViewModel>>() {
                override fun onChanged(sender: ObservableList<CategoryViewModel>?) {
                    notifyDataSetChanged()
                }
                override fun onItemRangeInserted(sender: ObservableList<CategoryViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeInserted(positionStart, itemCount)
                }
                override fun onItemRangeChanged(sender: ObservableList<CategoryViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeChanged(positionStart, itemCount)
                }
                override fun onItemRangeRemoved(sender: ObservableList<CategoryViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeRemoved(positionStart, itemCount)
                }
                override fun onItemRangeMoved(sender: ObservableList<CategoryViewModel>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }
            })
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemCategoryBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding.apply {
                viewModel = getItem(position)
                iconReorderGroup.setOnTouchListener { _, motionEvent ->
                    if(isMotionEventDown(motionEvent)) onStartDrag(holder)
                    false
                }
            }
            binding.cardView.setOnClickListener { showUpdateDialog(binding.viewModel) }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCategoryBinding> {
            return BindingHolder(context, parent, R.layout.item_category)
        }

        private fun isMotionEventDown(motionEvent: MotionEvent): Boolean {
            return (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN)
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
