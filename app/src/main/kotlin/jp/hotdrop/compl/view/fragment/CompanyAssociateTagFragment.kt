package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayoutManager
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.databinding.FragmentCompanyAssociateTagBinding
import jp.hotdrop.compl.databinding.ItemTagAssociateBinding
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.viewmodel.TagAssociateViewModel
import javax.inject.Inject

class CompanyAssociateTagFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var tagDao: TagDao
    @Inject
    lateinit var companyDao: CompanyDao

    private lateinit var binding: FragmentCompanyAssociateTagBinding
    private lateinit var adapter: FlexboxItemAdapter

    private val companyId by lazy { arguments.getInt(EXTRA_COMPANY_ID) }
    private val colorName by lazy { arguments.getString(EXTRA_COLOR_NAME) }

    companion object {
        private val EXTRA_COLOR_NAME = "colorName"
        fun create(companyId: Int, colorName: String) = CompanyAssociateTagFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_COMPANY_ID, companyId)
                putString(EXTRA_COLOR_NAME, colorName)
            }
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
        val disposable = tagDao.findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { tags -> onLoadSuccess(tags) },
                        { throwable -> showErrorAsToast(ErrorType.LoadFailureTags, throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(tags: List<Tag>) {
        adapter = FlexboxItemAdapter(context)

        adapter.addAll(tags.map{ TagAssociateViewModel(companyId, it, context, companyDao) })

        binding.recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = FlexboxLayoutManager()
            it.adapter = adapter
        }

        binding.fabDone.let {
            it.backgroundTintList = ColorStateList.valueOf(ColorUtil.getResDark(colorName, context))
            it.setOnClickListener {
                companyDao.associateTagByCompany(companyId, adapter.getAssociateModels())
                val intent = Intent().apply { putExtra(REFRESH_MODE, UPDATE) }
                activity.setResult(Activity.RESULT_OK, intent)
                exit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
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
                vm.changeAssociateState()
                binding.let {
                    it.borderLeft.setBackgroundColor(vm.getColorRes())
                    it.borderRight.setBackgroundColor(vm.getColorRes())
                    it.cardView.setCardBackgroundColor(vm.getBackGroundColorRes())
                }
            }
        }

        fun getAssociateModels(): List<Tag> {
            return list.filter { it.isAssociate }.map { it.tag }.toMutableList()
        }
    }
}