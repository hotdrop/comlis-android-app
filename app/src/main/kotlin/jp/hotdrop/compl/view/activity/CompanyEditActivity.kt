package jp.hotdrop.compl.view.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityCompanyEditBinding
import jp.hotdrop.compl.view.fragment.CompanyEditFragment

class CompanyEditActivity: BaseActivity() {

    companion object {
        @JvmStatic val EXTRA_COMPANY_ID = "companyId"
        fun startForResult(fragment: Fragment, companyId: Int, requestCode: Int) {
            val intent = Intent(fragment.context, CompanyEditActivity::class.java).apply {
                putExtra(EXTRA_COMPANY_ID, companyId)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCompanyEditBinding>(this, R.layout.activity_company_edit)
        getComponent().inject(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.title = binding.toolbar.title
            it.setDisplayHomeAsUpEnabled(true)
        }

        val companyId = intent.getIntExtra(EXTRA_COMPANY_ID, -1)
        replaceFragment(CompanyEditFragment.create(companyId), R.id.content_view)
    }
}
