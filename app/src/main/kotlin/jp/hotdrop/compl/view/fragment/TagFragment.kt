package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
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
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.view.parts.ColorSpinner
import jp.hotdrop.compl.viewmodel.TagViewModel
import javax.inject.Inject

/**
 * Company情報に付与するタグを登録/更新するFragment
 * タグっぽくしたかったのでalphaだがFlexbox-layoutのRecyclerView版を使用して実装した。
 * 本当はItemTouchHelperで上下左右好きな所にタグをdragして並び順変更したかった。
 * しかし、notifyMovedのところでRecyclerViewの中のitem表示が重複したり消えたりおかしな動きをしてどうしても解消できなかった。
 * そもそもItemTouchHelperはLayoutManagerの特定条件下に特化して作られているっぽいのでそれなりに動くが挙動が結構おかしいという
 * 状態になるのは仕方ないのかもしれない。
 * カスタムTouchHelperを作成すればできるかもしれないが、そのルートは沼がたくさんありそうなので今回はやめた。
 * 従って、並び順変更は専用の画面を作る。ならCategoryFragmentみたいに最初からRecyclerViewでいいのでは？となるかもしれないが
 * Flexbox-Layoutが思ったより簡単に導入できて良い感じに使えたのでぜひ使いたくてそのままにした。
 * 後、Categoryと同じUIにするとCategoryだかTagだか分からなくなりそうなので、初期画面ではなるべくTagであることをイメージさせる画面が
 * いいなとも思ってこのような作りにした。
 */
class TagFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var binding: FragmentTagBinding
    private lateinit var adapter: FlexItemAdapter

    companion object {
        @JvmStatic val TAG: String = TagFragment::class.java.simpleName
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

        if(adapter.itemCount > 0) {
            goneInitView()
            binding.fabViewOrder.visibility = View.VISIBLE
        } else {
            visibleInitView()
            binding.fabViewOrder.visibility = View.GONE
        }

        binding.fabViewOrder.setOnClickListener { ActivityNavigator.showTagViewOrder(this@TagFragment, REQ_CODE_TAG_VIEW_ORDER) }
        binding.fab.setOnClickListener { showRegisterDialog() }
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load tags." + e.message, Toast.LENGTH_LONG).show()
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
                    TagDao.insert(Tag().apply {
                        name = editText.text.toString()
                        colorType = spinner.getSelection()
                    })
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

    inner class FlexItemAdapter(context: Context)
        : ArrayRecyclerAdapter<TagViewModel, BindingHolder<ItemTagBinding>>(context) {

        override fun onBindViewHolder(holder: BindingHolder<ItemTagBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding
            binding.viewModel = getItem(position)
            binding.cardView.setOnClickListener { showUpdateDialog(binding.viewModel) }
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemTagBinding> {
            return BindingHolder(context, parent, R.layout.item_tag)
        }

        /**
         * Categoryと全く同じことやってるので
         * これらはViewModelにメソッド実装してArrayRecyclerAdapterの中で実装したほうがいいのでは・・
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
}