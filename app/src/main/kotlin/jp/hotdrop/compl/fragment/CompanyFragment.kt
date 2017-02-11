package jp.hotdrop.compl.fragment

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.FragmentCompanyItemBinding
import jp.hotdrop.compl.databinding.FragmentCompanyListBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import javax.inject.Singleton

@Singleton
class CompanyFragment : BaseFragment() {

    private lateinit var helper: ItemTouchHelper
    private lateinit var binding: FragmentCompanyListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return null
    }

    inner class CompanyAdapter(context: Context)
        : ArrayRecyclerAdapter<Company, BindingHolder<FragmentCompanyItemBinding>>(context) {

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): BindingHolder<FragmentCompanyItemBinding> {
            return BindingHolder(getContext(), parent!!, R.layout.fragment_company_item)
        }

        override fun onBindViewHolder(holder: BindingHolder<FragmentCompanyItemBinding>?, position: Int) {
            var company: Company = getItem(position)
            var binding: FragmentCompanyItemBinding = holder!!.binding
            binding.company = company

            // TODO clicklistener
        }

    }
}
