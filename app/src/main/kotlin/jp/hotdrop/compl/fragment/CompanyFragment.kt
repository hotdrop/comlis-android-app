package jp.hotdrop.compl.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.CompanyItemBinding
import jp.hotdrop.compl.databinding.FragmentCompanyListBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder

class CompanyFragment : BaseFragment() {

    // リストのアイテムタップ時のコールバックヘルパー
    private lateinit var helper: ItemTouchHelper
    private lateinit var binding: FragmentCompanyListBinding
    private lateinit var adapter: CompanyAdapter

    companion object {
        fun newInstance(): CompanyFragment {
            return CompanyFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyListBinding.inflate(inflater, container, false)
        adapter = CompanyAdapter(context)

        // TODO タップ動作
        //helper = ItemTouchHelper()

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter.addAll(dummyList())

        // TODO fabの動作

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun dummyList(): List<Company> {
        // サンプル
        var comp1 = Company()
        comp1.name = "サンプル１"
        comp1.member = "10"
        var comp2 = Company()
        comp2.name = "テストその２"
        comp2.member = "20"
        return mutableListOf(comp1, comp2)
    }

    /**
     * アダプター
     */
    inner class CompanyAdapter(context: Context)
        : ArrayRecyclerAdapter<Company, BindingHolder<CompanyItemBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<CompanyItemBinding> {
            return BindingHolder(getContext(), parent!!, R.layout.company_item)
        }

        override fun onBindViewHolder(holder: BindingHolder<CompanyItemBinding>?, position: Int) {
            var company: Company = getItem(position)
            var binding: CompanyItemBinding = holder!!.binding
            binding.company = company

            // TODO clicklistener
        }

    }
}
