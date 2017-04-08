package jp.hotdrop.compl.view.activity

import android.support.v4.app.Fragment

object ActivityNavigator {

    fun showCompanyRegister(fragment: Fragment, tabName: String, requestCode: Int) {
        CompanyRegisterActivity.startForResult(fragment, tabName, requestCode)
    }

    fun showCompanyDetail(fragment: Fragment, companyId: Int, requestCode: Int) {
        CompanyDetailActivity.startForResult(fragment, companyId, requestCode)
    }

    fun showCompanyAssociateTag(fragment: Fragment, companyId: Int, requestCode: Int) {
        CompanyAssociateTagActivity.startForResult(fragment, companyId, requestCode)
    }

    fun showCompanyEdit(fragment: Fragment, companyId: Int, requestCode: Int) {
        CompanyEditActivity.startForResult(fragment, companyId, requestCode)
    }

    fun showTagViewOrder(fragment: Fragment, requestCode: Int) {
        TagViewOrderActivity.startForResult(fragment, requestCode)
    }
}