package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentCompanyTabBinding
import jp.hotdrop.compl.databinding.ItemCompanyBinding
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import jp.hotdrop.compl.viewmodel.CompanyViewModel
import org.parceler.Parcels

class CompanyTabFragment: BaseFragment() {

    private lateinit var companyList: MutableList<CompanyViewModel>
    private lateinit var binding: FragmentCompanyTabBinding
    private lateinit var adapter: Adapter

    companion object {
        @JvmStatic val TAG = CompanyTabFragment::class.java.simpleName!!
        fun newInstance(itemList: MutableList<CompanyViewModel>) =
                CompanyTabFragment().apply {
                    arguments = Bundle().itemList(itemList)
                }
        private fun Bundle.itemList(itemList: MutableList<CompanyViewModel>) = apply { putParcelable(TAG, Parcels.wrap(itemList)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        companyList = Parcels.unwrap(arguments.getParcelable(TAG))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyTabBinding.inflate(inflater, container, false)

        adapter = Adapter(context)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter.addAll(companyList)

        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_COMPANY_UPDATE || data == null) {
            return
        }

        // TODO カテゴリーを更新した場合、面倒臭いのでTabを全リフレッシュする
        // TODO カテゴリーを更新しなかった場合、該当するアイテムのみ更新する
    }

    fun scrollUpToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    /**
     * アダプター
     */
    private inner class Adapter(context: Context):
            ArrayRecyclerAdapter<CompanyViewModel, BindingHolder<ItemCompanyBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCompanyBinding> {
            return BindingHolder(context, parent, R.layout.item_company)
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemCompanyBinding>?, position: Int) {
            val binding = holder!!.binding
            binding.viewModel = getItem(position)
            binding.cardView.setOnClickListener { v ->
                // TODO
            }
        }

        private fun refresh(vm: CompanyViewModel) {
            (0..adapter.itemCount).forEach { i ->
                val o = getItem(i)
                if(vm == o) {
                    o.change(vm)
                    adapter.notifyItemChanged(i)
                }
            }
        }

        fun add(vm: CompanyViewModel) {
            adapter.addItem(vm)
            adapter.notifyItemInserted(adapter.itemCount)
        }
    }
}