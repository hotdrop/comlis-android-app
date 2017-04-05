package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import android.widget.Toast
import com.google.android.flexbox.FlexboxLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.databinding.FragmentTagBinding
import jp.hotdrop.compl.databinding.ItemTagBinding
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.view.parts.ColorSpinner
import jp.hotdrop.compl.viewmodel.TagViewModel
import javax.inject.Inject

class TagFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var binding: FragmentTagBinding
    private lateinit var adapter: FlexItemAdapter
    private lateinit var helper: ItemTouchHelper

    companion object {
        @JvmStatic val TAG = TagFragment::class.java.simpleName!!
        fun newInstance() = TagFragment()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTagBinding.inflate(inflater, container, false)
        loadData()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_TAG_VIEW_ORDER || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, REFRESH_NONE)
        if(refreshMode == REFRESH) {
            loadData()
            activity.intent.removeExtra(REFRESH_MODE)
        }
    }

    private fun loadData() {
        val disposable = TagDao.findAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { tags -> onLoadSuccess(tags) },
                        { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(tags: List<Tag>) {

        adapter = FlexItemAdapter(context)

        if(tags.isNotEmpty()) {
            adapter.addAll(tags.map { t -> TagViewModel(t, context) })
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = FlexboxLayoutManager()
        binding.recyclerView.adapter = adapter

        helper = ItemTouchHelper(TagItemTouchHelperCallback(adapter))
        binding.recyclerView.addItemDecoration(helper)
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

    private fun visibleInitView() {
        binding.listEmptyView.visibility = View.VISIBLE
    }

    private fun goneInitView() {
        binding.listEmptyView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
                    val editTxt = editText.text.toString()
                    when {
                        editTxt == "" -> disableButton()
                        tagId == REGISTER_MODE -> {
                            if(TagDao.exist(editTxt)) {
                                disableButtonWithAttention()
                            } else {
                                enableButton()
                            }
                        }
                        else -> {
                            if(editTxt != originName && TagDao.exist(editTxt, tagId)) {
                                disableButtonWithAttention()
                            } else {
                                enableButton()
                            }
                        }
                    }
                }
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
                    TagDao.insert(editText.text.toString(), spinner.getSelection())
                    val tag = TagDao.find(editText.text.toString())
                    adapter.add(TagViewModel(tag, context))
                    dialogInterface.dismiss()
                    goneInitView()
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
        spinner.setSelection(vm.tag.colorType)
        val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                .setView(view)
                .setPositiveButton(R.string.dialog_update_button, { dialogInterface, _ ->
                    vm.viewName = editText.text.toString()
                    vm.tag.colorType = spinner.getSelection()
                    TagDao.update(vm.makeTag())
                    adapter.refresh(vm)
                    dialogInterface.dismiss()
                })
                .setNegativeButton(R.string.dialog_delete_button, { dialogInterface, _ ->
                    TagDao.delete(vm.tag)
                    loadData()
                    dialogInterface.dismiss()
                })
                .create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
        editText.changeTextListener(view, dialog, editText, vm.tag.id, vm.viewName)
    }

    private fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        helper.startDrag(viewHolder)
    }


    inner class FlexItemAdapter(context: Context)
        : ArrayRecyclerAdapter<TagViewModel, BindingHolder<ItemTagBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemTagBinding> {
            return BindingHolder(context, parent, R.layout.item_tag)
        }

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

        /**
         * Categoryと全く同じことやってるので
         * これらはViewModelに実装してArrayRecyclerAdapterの中で実装したほうがいいのでは・・
         */
        fun refresh(vm: TagViewModel) {
            (0..itemCount - 1).forEach { i ->
                val c = getItem(i)
                if(vm == c) {
                    c.change(vm)
                    notifyItemChanged(i)
                }
            }
        }

        fun add(vm: TagViewModel) {
            addItem(vm)
            notifyItemInserted(itemCount)
        }
    }

    /**
     * アイテム選択時のコールバッククラス
     */
    inner class TagItemTouchHelperCallback(val adapter: FlexItemAdapter): ItemTouchHelper.Callback() {

        override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
            val dragFrags: Int = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = 0
            return makeMovementFlags(dragFrags, swipeFlags)
        }

        override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
            if(viewHolder == null || target == null) {
                return false
            }
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