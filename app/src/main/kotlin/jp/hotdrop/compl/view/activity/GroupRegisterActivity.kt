package jp.hotdrop.compl.view.activity

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityGroupRegisterBinding
import jp.hotdrop.compl.view.fragment.GroupRegisterFragment

class GroupRegisterActivity : BaseActivity() {

    companion object {

        fun startForResult(fragment: Fragment, requestCode: Int) {
            val intent = createIntent(fragment.context)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun createIntent(context: Context) = Intent(context, GroupRegisterActivity::class.java).apply {  }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityGroupRegisterBinding>(this, R.layout.activity_group_register)
        getComponent().inject(this)

        replaceFragment(GroupRegisterFragment.create(), R.id.content_view)
    }
}
