package jp.hotdrop.compl.view.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.view.MotionEventCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import com.google.android.flexbox.FlexboxLayoutManager
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentTagBinding
import jp.hotdrop.compl.databinding.ItemTagBinding
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.parts.ColorSpinner
import jp.hotdrop.compl.viewmodel.TagViewModel
import jp.hotdrop.compl.viewmodel.TagsViewModel
import javax.inject.Inject

class TagFragment: BaseFragment(), TagsViewModel.Callback {

    @Inject
    lateinit var viewModel: TagsViewModel

    private lateinit var binding: FragmentTagBinding
    private lateinit var adapter: FlexItemAdapter
    private lateinit var helper: ItemTouchHelper

    companion object {
        val TAG: String = TagFragment::class.java.simpleName
        fun newInstance() = TagFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setCallBack(this)
        viewModel.loadData()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTagBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        initView()
        return binding.root
    }

    private fun initView() {
        adapter = FlexItemAdapter(context)
        helper = ItemTouchHelper(TagItemTouchHelperCallback(adapter))

        binding.recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = FlexboxLayoutManager()
            it.adapter = adapter
            it.addItemDecoration(helper)
        }

        helper.attachToRecyclerView(binding.recyclerView)
        binding.fabButton.setOnClickListener { showRegisterDialog() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateItemOrder()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }

    override fun showError(throwable: Throwable) {
        showErrorAsToast(ErrorType.LoadFailureTags, throwable)
    }

    private fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    /**
     * ダイアログで、入力した分類名に応じてボタンと注意書きの制御を行う拡張関数
     */
    private val REGISTER_MODE: Int = -1
    fun AppCompatEditText.changeTextListener(view: View, dialog: AlertDialog, editText: AppCompatEditText,
                                             tagId: Int = REGISTER_MODE, originName: String = "") =
            addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/*no op*/}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*no op*/ }
                override fun afterTextChanged(s: Editable?) {
                    when {
                        editText.text.toString() == "" -> disableButton()
                        tagId == REGISTER_MODE -> if(existName()) disableButtonWithAttention() else enableButton()
                        else -> if(existNameExclusionOwn()) disableButtonWithAttention() else enableButton()
                    }
                }
                private fun existName(): Boolean = viewModel.existName(editText.text.toString())
                private fun existNameExclusionOwn(): Boolean =
                        (editText.text.toString() != originName && viewModel.existNameExclusionId(editText.text.toString(), tagId))
                private fun disableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById(R.id.label_tag_attention).visibility = View.GONE
                }
                private fun disableButtonWithAttention() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById(R.id.label_tag_attention).visibility = View.VISIBLE
                }
                private fun enableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    view.findViewById(R.id.label_tag_attention).visibility = View.GONE
                }
            })

    private fun showRegisterDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_tag, null)
        val editText = view.findViewById(R.id.text_tag_name) as AppCompatEditText
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

    private fun showUpdateDialog(vm: TagViewModel) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_tag, null)
        val editText = view.findViewById(R.id.text_tag_name) as AppCompatEditText
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
        if(vm.isAttachCompany()) {
            view.findViewById(R.id.label_tag_delete_attention).visibility = View.VISIBLE
        }
        editText.changeTextListener(view, dialog, editText, vm.getId(), vm.viewName)
    }

    inner class FlexItemAdapter(context: Context)
        : ArrayRecyclerAdapter<TagViewModel, BindingHolder<ItemTagBinding>>(context) {

        override fun onBindViewHolder(holder: BindingHolder<ItemTagBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding
            binding.viewModel = getItem(position)
            binding.cardView.setOnTouchListener { _, motionEvent ->
                if(MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder)
                }
                false
            }
            binding.cardView.setOnClickListener { showUpdateDialog(binding.viewModel) }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemTagBinding> {
            return BindingHolder(context, parent, R.layout.item_tag)
        }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class TagItemTouchHelperCallback(val adapter: FlexItemAdapter): ItemTouchHelper.Callback() {

        val NONE_POSITION = -1
        var fromPosition = NONE_POSITION
        var toPosition = NONE_POSITION

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFrags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            return makeMovementFlags(dragFrags, swipeFlags)
        }

        /**
         * flexbox-layoutを使用したアイテムのドラッグについて
         * 上下の場合は常に互いのアイテムをswapさせれば問題なかったがflexboxは上下でアイテムを飛び越えられるので
         * 隣接するアイテムのswapでは対応できない。
         * 従って、アイテムの位置情報dragFromとdragTをフィールドで保持し、ドラッグ完了後のclearViewで実際のリストを更新する
         */
        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            if(viewHolder == null || target == null) {
                return false
            }
            if(fromPosition == NONE_POSITION) {
                fromPosition = viewHolder.adapterPosition
            }
            toPosition = target.adapterPosition
            adapter.onItemMovedForFlexBox(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
            return
        }

        override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
            super.clearView(recyclerView, viewHolder)
            if(fromPosition != NONE_POSITION && toPosition != NONE_POSITION && fromPosition != toPosition) {
                adapter.onListUpdateForFlexBox(fromPosition, toPosition)
            }
            fromPosition = NONE_POSITION
            toPosition = NONE_POSITION
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }

    }
}