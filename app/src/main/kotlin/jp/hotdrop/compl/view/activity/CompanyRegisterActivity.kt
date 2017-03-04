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
        fun startForResult(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.context, CompanyRegisterActivity::class.java).apply{}
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityCompanyRegisterBinding>(this, R.layout.activity_company_register)
        getComponent().inject(this)
        replaceFragment(CompanyRegisterFragment.create(), R.id.content_view)
    }
}
