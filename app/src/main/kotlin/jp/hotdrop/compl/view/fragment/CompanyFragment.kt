package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import jp.hotdrop.compl.R
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.databinding.CompanyItemBinding
import jp.hotdrop.compl.databinding.FragmentCompanyListBinding
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.view.ArrayRecyclerAdapter
import jp.hotdrop.compl.view.BindingHolder
import org.parceler.Parcels
import javax.inject.Inject


class CompanyFragment : BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var binding: FragmentCompanyListBinding
    private lateinit var adapter: Adapter

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
        adapter = Adapter(context)

        // TODO タップ動作
        //helper = ItemTouchHelper()

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)

        loadData()

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
        adapter.add(company)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    private fun loadData() {
        var disposable = CompanyDao.findAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { list -> onLoadSuccess(list) },
                        { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onLoadSuccess(companies: List<Company>) {
        adapter.addAll(companies)
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load companies.", Toast.LENGTH_LONG).show()
    }

    /**
     * アダプター
     */
    private inner class Adapter(context: Context)
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

        fun add(company: Company) {
            adapter.addItem(company)
            adapter.notifyItemInserted(adapter.itemCount)
        }
    }
}
