package jp.hotdrop.comlis.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.databinding.ObservableList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.google.android.flexbox.FlexboxLayoutManager
import jp.hotdrop.comlis.R
import jp.hotdrop.comlis.databinding.FragmentCompanyAssociateTagBinding
import jp.hotdrop.comlis.databinding.ItemTagAssociateBinding
import jp.hotdrop.comlis.util.ColorUtil
import jp.hotdrop.comlis.view.ArrayRecyclerAdapter
import jp.hotdrop.comlis.view.BindingHolder
import jp.hotdrop.comlis.viewmodel.TagAssociateViewModel
import jp.hotdrop.comlis.viewmodel.TagsAssociateViewModel
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

        binding.recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = FlexboxLayoutManager(context)
            it.adapter = adapter
            it.layoutAnimation = AnimationUtils.loadLayoutAnimation(context, R.anim.tag_layout)
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

    override fun onDestroy() {
        super.onDestroy()
        viewModel.destroy()
    }

    override fun showError(throwable: Throwable) {
        showErrorAsToast(ErrorType.LoadFailureTags, throwable)
    }

    /**
     * このFragmentクラスだけObservableで変更を通知する。
     *
     * 本当はリストを使用している画面は全てこの形式にしたかったのだが、CardViewをタップで並び順変更できるようにしている画面が多数存在する。
     * これらの画面（具体的にはItemTouchHelperCallbackを実装している画面）でBaseObservableを継承したViewModel経由でリスト変更通知を
     * 実装すると画面がチカチカしたり、重かったりとうまくいかなかった。（モーションのブレ、残像、アイテムの分身なども発生した）
     *
     * 本当はこれをやると設計が不統一になって可読性やメンテナンス性が下がるのでよくないが、このアプリはプロダクトではないしそもそも
     * 勉強を兼ねてる個人向けなのでこの画面でのみ適用する。
     */
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

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemTagAssociateBinding> =
                BindingHolder(context, parent, R.layout.item_tag_associate)

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