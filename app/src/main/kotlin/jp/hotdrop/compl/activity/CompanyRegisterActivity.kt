package jp.hotdrop.compl.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityCompanyRegisterBinding

class CompanyRegisterActivity : BaseActivity() {

    companion object {
        fun startForResult(fragment: Fragment, requestCode: Int) {
            val intent = createIntent(fragment.context)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun createIntent(context: Context): Intent {
            val intent = Intent(context, this.javaClass)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityCompanyRegisterBinding>(this, R.layout.activity_company_register)
        getComponent().inject(this)

        //replaceFragment()
    }
}
