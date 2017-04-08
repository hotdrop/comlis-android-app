package jp.hotdrop.compl.view.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.compl.R
import jp.hotdrop.compl.databinding.ActivityTagViewOrderBinding
import jp.hotdrop.compl.view.fragment.TagViewOrderFragment

class TagViewOrderActivity: BaseActivity() {

    companion object {
        fun startForResult(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.context, TagViewOrderActivity::class.java).apply {  }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityTagViewOrderBinding>(this, R.layout.activity_tag_view_order)
        getComponent().inject(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            it.title = binding.toolbar.title
            it.setDisplayHomeAsUpEnabled(true)
        }

        replaceFragment(TagViewOrderFragment.create(), R.id.content_view)
    }
}