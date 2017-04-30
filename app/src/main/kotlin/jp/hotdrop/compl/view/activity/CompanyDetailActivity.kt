package jp.hotdrop.compl.view.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityCompanyDetailBinding
import jp.hotdrop.compl.view.fragment.CompanyDetailFragment

class CompanyDetailActivity: BaseActivity() {

    companion object {
        val EXTRA_COMPANY_ID = "companyId"
        fun startForResult(fragment: Fragment, companyId: Int, requestCode: Int) {
            val intent = Intent(fragment.context, CompanyDetailActivity::class.java).apply {
                putExtra(EXTRA_COMPANY_ID, companyId)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityCompanyDetailBinding>(this, R.layout.activity_company_detail)
        getComponent().inject(this)
        val companyId = intent.getIntExtra(EXTRA_COMPANY_ID, 0)
        replaceFragment(CompanyDetailFragment.create(companyId), R.id.content_view)
    }

}