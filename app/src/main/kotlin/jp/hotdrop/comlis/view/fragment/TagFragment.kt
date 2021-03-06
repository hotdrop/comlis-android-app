package jp.hotdrop.comlis.view.fragment

import android.content.Context
import android.os.Bundle
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
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.comlis.R
import jp.hotdrop.comlis.databinding.FragmentTagBinding
import jp.hotdrop.comlis.databinding.ItemTagBinding
import jp.hotdrop.comlis.model.Tag
import jp.hotdrop.comlis.view.ArrayRecyclerAdapter
import jp.hotdrop.comlis.view.BindingHolder
import jp.hotdrop.comlis.view.parts.ColorSpinner
import jp.hotdrop.comlis.viewmodel.TagViewModel
import jp.hotdrop.comlis.viewmodel.TagsViewModel
import javax.inject.Inject

class TagFragment: BaseFragment() {

    @Inject
    lateinit var viewModel: TagsViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTagBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        loadData()
        return binding.root
    }

    private fun loadData() {
        viewModel.getData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { initView(it) },
                        onError = { showErrorAsToast(ErrorType.LoadFailureTags, it) }
                )
                .addTo(compositeDisposable)
    }

    private fun initView(tagViewModels: List<TagViewModel>) {
        adapter = FlexItemAdapter(context)

        if(tagViewModels.isNotEmpty()) {
            adapter.addAll(tagViewModels)
            viewModel.goneEmptyMessageOnScreen()
        } else {
            viewModel.visibilityEmptyMessageOnScreen()
        }

        helper = ItemTouchHelper(TagItemTouchHelperCallback(adapter))
        binding.recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = FlexboxLayoutManager(context)
            it.adapter = adapter
            it.addItemDecoration(helper)
            it.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.tag_layout)
        }

        helper.attachToRecyclerView(binding.recyclerView)
        binding.fabButton.setOnClickListener { showRegisterDialog() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.updateItemOrder(adapter.getModels())
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }

    /**
     * ダイアログで、入力した分類名に応じてボタンと注意書きの制御を行う拡張関数
     */
    private val REGISTER_MODE: Int = -1
    private fun AppCompatEditText.changeTextListener(view: View,
                                                     dialog: AlertDialog,
                                                     editText: AppCompatEditText,
                                                     tagId: Int = REGISTER_MODE,
                                                     originName: String = "") =
            addTextChangedListener(object: TextWatcher {

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {/*no op*/}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {/*no op*/ }

                override fun afterTextChanged(s: Editable?) {
                    when {
                        editText.toText() == "" -> disableButton()
                        tagId == REGISTER_MODE -> if(existName()) disableButtonWithAttention() else enableButton()
                        else -> if(existNameExclusionOwn()) disableButtonWithAttention() else enableButton()
                    }
                }

                private fun existName(): Boolean =
                        viewModel.existName(editText.toText())

                private fun existNameExclusionOwn(): Boolean =
                        (editText.toText() != originName && viewModel.existNameExclusionId(editText.toText(), tagId))

                private fun disableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById<TextView>(R.id.label_tag_attention).visibility = View.GONE
                }

                private fun disableButtonWithAttention() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                    view.findViewById<TextView>(R.id.label_tag_attention).visibility = View.VISIBLE
                }

                private fun enableButton() {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    view.findViewById<TextView>(R.id.label_tag_attention).visibility = View.GONE
                }
            })

    private fun showRegisterDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_tag, null)
        val editText = view.findViewById<AppCompatEditText>(R.id.text_tag_name)

        val spinner = ColorSpinner(view.findViewById(R.id.spinner_color_type), context)

        context?.run {
            val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setView(view)
                    .setPositiveButton(R.string.dialog_add_button, { dialogInterface, _ ->
                        viewModel.register(editText.toText(), spinner.getSelection())
                        viewModel.getViewModel(editText.toText())?.let { adapter.add(it) }
                        if(adapter.itemCount == 1) {
                            viewModel.goneEmptyMessageOnScreen()
                        }
                        dialogInterface.dismiss()
                    })
                    .create()

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
            editText.changeTextListener(view, dialog, editText)
        }
    }

    private fun showUpdateDialog(vm: TagViewModel) {
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_tag, null)
        val editText = (view.findViewById<AppCompatEditText>(R.id.text_tag_name)).apply { setText(vm.viewName as CharSequence) }

        val spinner = ColorSpinner(view.findViewById(R.id.spinner_color_type), context).apply { setSelection(vm.getColorType()) }

        context?.run {
            val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
                    .setView(view)
                    .setPositiveButton(R.string.dialog_update_button, { dialogInterface, _ ->
                        viewModel.update(vm, editText.toText(), spinner.getSelection())
                        viewModel.getViewModel(editText.toText())?.let { adapter.refresh(it) }

                        dialogInterface.dismiss()
                    })
                    .setNegativeButton(R.string.dialog_delete_button, { dialogInterface, _ ->
                        viewModel.delete(vm)
                        adapter.remove(vm)

                        if(adapter.itemCount <= 0) {
                            viewModel.visibilityEmptyMessageOnScreen()
                        }

                        dialogInterface.dismiss()
                    })
                    .create()

            dialog.show()
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
            if(vm.isAttachCompany()) {
                view.findViewById<TextView>(R.id.label_tag_delete_attention).visibility = View.VISIBLE
            }
            editText.changeTextListener(view, dialog, editText, vm.getId(), vm.viewName)
        }
    }

    inner class FlexItemAdapter(context: Context?)
        : ArrayRecyclerAdapter<TagViewModel, BindingHolder<ItemTagBinding>>(context) {

        override fun onBindViewHolder(holder: BindingHolder<ItemTagBinding>, position: Int) {

            val binding = holder.binding
            binding.viewModel = getItem(position)

            binding.cardView.setOnTouchListener { _, motionEvent ->
                if(motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    onStartDrag(holder)
                }
                false
            }
            binding.viewModel?.let { vm ->
                binding.cardView.setOnClickListener { showUpdateDialog(vm) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder<ItemTagBinding> =
                BindingHolder(context, parent, R.layout.item_tag)

        fun refresh(vm: TagViewModel) {
            val position = adapter.getItemPosition(vm) ?: return
            adapter.getItem(position).change(vm)
            notifyItemChanged(position)
        }

        fun add(vm: TagViewModel) {
            addItem(vm)
            notifyItemInserted(itemCount)
        }

        fun remove(vm: TagViewModel) {
            val position = adapter.getItemPosition(vm) ?: return
            adapter.removeItem(position)
            notifyItemRemoved(position)
        }

        fun getModels(): List<Tag> =
                (0 until adapter.itemCount)
                    .map { adapter.getItem(it) }
                    .map { it.tag }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class TagItemTouchHelperCallback(val adapter: FlexItemAdapter): ItemTouchHelper.Callback() {

        private val NONE_POSITION = -1
        private var fromPosition = NONE_POSITION
        private var toPosition = NONE_POSITION

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

        override fun isLongPressDragEnabled() = false
    }
}
