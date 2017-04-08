package jp.hotdrop.compl.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.flexbox.FlexboxLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.databinding.FragmentCompanyAssociateTagBinding
import jp.hotdrop.compl.databinding.ItemTagAssociateBinding
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.viewmodel.TagAssociateViewModel
import javax.inject.Inject

class CompanyAssociateTagFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var binding: FragmentCompanyAssociateTagBinding
    private lateinit var adapter: FlexboxItemAdapter

    private val companyId by lazy {
        arguments.getInt(EXTRA_COMPANY_ID)
    }

    companion object {
        @JvmStatic val EXTRA_COMPANY_ID = "companyId"
        fun create(companyId: Int) = CompanyAssociateTagFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_COMPANY_ID, companyId) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyAssociateTagBinding.inflate(inflater, container, false)
        loadData()
        return binding.root
    }

    private fun loadData() {
        var disposable = TagDao.findAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { tags -> onLoadSuccess(tags) },
                        { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(tags: List<Tag>) {
        adapter = FlexboxItemAdapter(context)
        adapter.addAll(tags.map{ TagAssociateViewModel(it, context, false) })

        // TODO すでに登録されているものをどうやって識別するか・・

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = FlexboxLayoutManager()
        binding.recyclerView.adapter = adapter

        // TODO チェックボタンクリックイベント実装するでDBに反映する
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load tags." + e.message, Toast.LENGTH_LONG).show()
    }

    inner class FlexboxItemAdapter(context: Context)
        : ArrayRecyclerAdapter<TagAssociateViewModel, BindingHolder<ItemTagAssociateBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemTagAssociateBinding> {
            return BindingHolder(context, parent, R.layout.item_tag_associate)
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemTagAssociateBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding
            binding.viewModel = getItem(position)

            binding.cardView.setOnClickListener {
                val vm = binding.viewModel
                vm.changeAttachState()
                binding.let {
                    it.borderLeft.setBackgroundColor(vm.getColorRes())
                    it.borderRight.setBackgroundColor(vm.getColorRes())
                    it.cardView.setCardBackgroundColor(vm.getBackGroundColorRes())
                }
            }
        }
    }
}