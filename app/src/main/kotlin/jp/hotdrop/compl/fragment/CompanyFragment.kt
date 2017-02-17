package jp.hotdrop.compl.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.R
import jp.hotdrop.compl.activity.ActivityNavigator
import jp.hotdrop.compl.databinding.CompanyItemBinding
import jp.hotdrop.compl.databinding.FragmentCompanyListBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import org.parceler.Parcels


class CompanyFragment : BaseFragment() {

    private lateinit var binding: FragmentCompanyListBinding
    private lateinit var adapter: CompanyAdapter

    companion object {
        fun newInstance(): CompanyFragment = CompanyFragment()
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
        setHasOptionsMenu(true)
        adapter = CompanyAdapter(context)

        // TODO タップ動作
        //helper = ItemTouchHelper()

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter.addAll(dummyList())

        binding.fabAddButton.setOnClickListener { v ->
            ActivityNavigator.showCompanyRegister(this, REQ_CODE_COMPANY_REGISTER)
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_COMPANY_REGISTER || data == null) {
            return
        }

        val company = Parcels.unwrap<Company>(data.getParcelableExtra(CompanyRegisterFragment.TAG))
        adapter.addItem(company)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    /**
     * TODO 後で消す
     */
    private fun dummyList(): List<Company> {
        val comp1 = Company()
        comp1.name = "テスト名前その１です。"
        val comp2 = Company()
        comp2.name = "テストその２"
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

