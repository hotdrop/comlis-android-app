package jp.hotdrop.compl.view.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.databinding.FragmentCompanyDetailBinding
import jp.hotdrop.compl.viewmodel.CompanyDetailViewModel

class CompanyDetailFragment: BaseFragment() {

    private lateinit var binding: FragmentCompanyDetailBinding
    private lateinit var viewModel: CompanyDetailViewModel

    private val companyId by lazy {
        arguments.getInt(EXTRA_COMPANY_ID)
    }

    companion object {
        @JvmStatic val EXTRA_COMPANY_ID = "companyId"
        fun create(companyId: Int) = CompanyDetailFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_COMPANY_ID, companyId) }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        // TODO 更新ボタンつけるならここで
        initToolbar()
        initLayout()
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    private fun initToolbar() {
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeButtonEnabled(true)
        }
    }

    private fun initLayout() {
        viewModel = CompanyDetailViewModel(companyId)
        binding.viewModel = viewModel
        binding.fab.setOnClickListener {
            val checked = !binding.fab.isSelected
            binding.fab.isSelected = checked
        }
        //binding.imageUrl.setOnClickListener { onClickUrl() }
    }

    private fun onClickUrl() {
        if(viewModel.url != null) {
            val uri = Uri.parse(viewModel.url)
            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }
}