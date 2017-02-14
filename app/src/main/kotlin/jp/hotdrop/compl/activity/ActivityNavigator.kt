package jp.hotdrop.compl.activity

import android.support.v4.app.Fragment

object ActivityNavigator {

    fun showCompanyRegister(fragment: Fragment, requestCode: Int) {
        CompanyRegisterActivity.startForResult(fragment, requestCode)
    }
}