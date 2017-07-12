package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.databinding.ObservableList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.flexbox.FlexboxLayoutManager
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentCompanyAssociateTagBinding
import jp.hotdrop.compl.databinding.ItemTagAssociateBinding
import jp.hotdrop.compl.util.ColorUtil
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.viewmodel.TagAssociateViewModel
import jp.hotdrop.compl.viewmodel.TagsAssociateViewModel
import javax.inject.Inject

class CompanyAssociateTagFragment: BaseFragment(), TagsAssociateViewModel.Callback {

    @Inject
    lateinit var viewModel: TagsAssociateViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setCallback(this)
        viewModel.loadData(companyId)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyAssociateTagBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        initView()
        return binding.root
    }

    private fun initView() {
        adapter = FlexboxItemAdapter(context, viewModel.viewModels)
        binding.recyclerView.run {
            setHasFixedSize(true)
            layoutManager = FlexboxLayoutManager()
            this.adapter = adapter
        }

        binding.fabDone.run {
            backgroundTintList = ColorStateList.valueOf(ColorUtil.getResDark(colorName, context))
            setOnClickListener {
                viewModel.update()
                val intent = Intent().apply { putExtra(REFRESH_MODE, UPDATE) }
                activity.setResult(Activity.RESULT_OK, intent)
                exit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.destroy()
    }

    override fun showError(throwable: Throwable) {
        showErrorAsToast(ErrorType.LoadFailureTags, throwable)
    }

    inner class FlexboxItemAdapter(context: Context, list: ObservableList<TagAssociateViewModel>)
        : ArrayRecyclerAdapter<TagAssociateViewModel, BindingHolder<ItemTagAssociateBinding>>(context, list) {

        init {
            list.addOnListChangedCallback(object: ObservableList.OnListChangedCallback<ObservableList<TagAssociateViewModel>>() {
                override fun onChanged(sender: ObservableList<TagAssociateViewModel>?) {
                    notifyDataSetChanged()
                }
                override fun onItemRangeInserted(sender: ObservableList<TagAssociateViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeInserted(positionStart, itemCount)
                }
                override fun onItemRangeChanged(sender: ObservableList<TagAssociateViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeChanged(positionStart, itemCount)
                }
                override fun onItemRangeRemoved(sender: ObservableList<TagAssociateViewModel>?, positionStart: Int, itemCount: Int) {
                    notifyItemRangeRemoved(positionStart, itemCount)
                }
                override fun onItemRangeMoved(sender: ObservableList<TagAssociateViewModel>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
                    notifyItemMoved(fromPosition, toPosition)
                }
            })
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemTagAssociateBinding> {
            return BindingHolder(context, parent, R.layout.item_tag_associate)
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemTagAssociateBinding>?, position: Int) {
            holder ?: return
            val binding = holder.binding.apply {
                viewModel = getItem(position)
            }

            binding.cardView.setOnClickListener {
                val vm = binding.viewModel
                vm.changeAssociateState()
                binding.run {
                    borderLeft.setBackgroundColor(vm.getColorRes())
                    borderRight.setBackgroundColor(vm.getColorRes())
                    cardView.setCardBackgroundColor(vm.getBackGroundColorRes())
                }
            }
        }
    }
}