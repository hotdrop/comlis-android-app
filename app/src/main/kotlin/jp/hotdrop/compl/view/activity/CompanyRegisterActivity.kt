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
        @JvmStatic val TAG = CompanyRegisterActivity::class.java.simpleName!!
        fun startForResult(fragment: Fragment, tabName: String, requestCode: Int) {
            val intent = Intent(fragment.context, CompanyRegisterActivity::class.java).apply{
                putExtra(TAG, tabName)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityCompanyRegisterBinding>(this, R.layout.activity_company_register)
        getComponent().inject(this)
        val tabName = intent.getStringExtra(TAG)
        replaceFragment(CompanyRegisterFragment.create(tabName), R.id.content_view)
    }
}
