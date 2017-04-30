package jp.hotdrop.compl.view.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityCompanyRegisterBinding
import jp.hotdrop.compl.view.fragment.CompanyRegisterFragment

class CompanyRegisterActivity : BaseActivity() {

    companion object {
        val EXTRA_TAB_NAME = "tabName"
        fun startForResult(fragment: Fragment, tabName: String?, requestCode: Int) {
            val intent = Intent(fragment.context, CompanyRegisterActivity::class.java).apply{
                putExtra(EXTRA_TAB_NAME, tabName)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityCompanyRegisterBinding>(this, R.layout.activity_company_register)
        getComponent().inject(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.title = binding.toolbar.title
            it.setDisplayHomeAsUpEnabled(true)
        }

        val tabName = intent.getStringExtra(EXTRA_TAB_NAME)
        replaceFragment(CompanyRegisterFragment.create(tabName), R.id.content_view)
    }
}
