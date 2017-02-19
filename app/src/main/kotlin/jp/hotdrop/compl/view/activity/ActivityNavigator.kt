package jp.hotdrop.compl.view.activity

import android.support.v4.app.Fragment

object ActivityNavigator {

    fun showCompanyRegister(fragment: Fragment, requestCode: Int) {
        CompanyRegisterActivity.startForResult(fragment, requestCode)
    }
}