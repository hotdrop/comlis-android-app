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
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import org.parceler.Parcels

class CompanyTabFragment: BaseFragment() {

    private lateinit var companyList: MutableList<Company>
    private lateinit var binding: FragmentCompanyTabBinding
    private val adapter by lazy {
        Adapter(context)
    }

    companion object {
        @JvmStatic val TAG = CompanyTabFragment::class.java.simpleName!!
        fun newInstance(itemList: MutableList<Company>) =
                CompanyTabFragment().apply {
                    arguments = Bundle().itemList(itemList)
                }
        private fun Bundle.itemList(itemList: MutableList<Company>) = apply { putParcelable(TAG, Parcels.wrap(itemList)) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        companyList = Parcels.unwrap(arguments.getParcelable(TAG))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyTabBinding.inflate(inflater, container, false)
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
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_COMPANY_REGISTER || data == null) {
            return
        }

        // TODO これだとタブに対応できないので考える
        val company = Parcels.unwrap<Company>(data.getParcelableExtra(CompanyRegisterFragment.TAG))
        adapter.add(company)
    }

    fun scrollUpToTop() {
        binding.recyclerView.smoothScrollToPosition(0)
    }

    /**
     * アダプター
     */
    private inner class Adapter(context: Context):
            ArrayRecyclerAdapter<Company, BindingHolder<ItemCompanyBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<ItemCompanyBinding> {
            return BindingHolder(context, parent, R.layout.item_company)
        }

        override fun onBindViewHolder(holder: BindingHolder<ItemCompanyBinding>?, position: Int) {
            val binding = holder!!.binding
            binding.company = getItem(position)
            binding.cardView.setOnClickListener { v ->
                // TODO
            }
        }

        private fun refresh(company: Company) {
            (0..adapter.itemCount).forEach { i ->
                val o = getItem(i)
                if(company.equals(o)) {
                    o.change(company)
                    adapter.notifyItemChanged(i)
                }
            }
        }

        fun add(company: Company) {
            adapter.addItem(company)
            adapter.notifyItemInserted(adapter.itemCount)
        }
    }
}