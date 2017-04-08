package jp.hotdrop.compl.view.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityCompanyAssociateTagBinding
import jp.hotdrop.compl.view.fragment.CompanyAssociateTagFragment

class CompanyAssociateTagActivity: BaseActivity() {

    companion object {
        @JvmStatic val EXTRA_COMPANY_ID = "companyId"
        fun startForResult(fragment: Fragment, companyId: Int, requestCode: Int) {
            val intent = Intent(fragment.context, CompanyAssociateTagActivity::class.java).apply {
                putExtra(EXTRA_COMPANY_ID, companyId)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityCompanyAssociateTagBinding>(this, R.layout.activity_company_associate_tag)
        getComponent().inject(this)
        val companyId = intent.getIntExtra(EXTRA_COMPANY_ID, 0)
        replaceFragment(CompanyAssociateTagFragment.create(companyId), R.id.content_view)
    }
}