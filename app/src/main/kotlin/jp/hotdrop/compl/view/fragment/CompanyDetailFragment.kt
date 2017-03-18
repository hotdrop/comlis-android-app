package jp.hotdrop.compl.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class CompanyDetailFragment: BaseFragment() {

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
        return super.onCreateView(inflater, container, savedInstanceState)
    }
}