package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.databinding.FragmentCompanyDetailBinding
import jp.hotdrop.compl.util.ColorUtil
import jp.hotdrop.compl.view.activity.ActivityNavigator
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

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        initToolbar()
        initLayout()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_COMPANY_EDIT || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, REFRESH_NONE)
        if(refreshMode == REFRESH) {
            refreshLayout()
            activity.intent = data
        }
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
        refreshLayout()
        binding.fab.setOnClickListener {
            ActivityNavigator.showCompanyEdit(this@CompanyDetailFragment, companyId, REQ_CODE_COMPANY_EDIT)
        }
    }

    private fun refreshLayout() {
        viewModel = CompanyDetailViewModel(companyId)
        binding.viewModel = viewModel
        binding.fab.backgroundTintList = ColorStateList.valueOf(ColorUtil.getResNormal(viewModel.colorName, context))
    }
}