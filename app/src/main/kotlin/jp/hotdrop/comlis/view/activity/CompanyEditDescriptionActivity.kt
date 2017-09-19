package jp.hotdrop.comlis.view.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import jp.hotdrop.comlis.R
import jp.hotdrop.comlis.databinding.ActivityCompanyEditDescriptionBinding
import jp.hotdrop.comlis.util.ColorUtil
import jp.hotdrop.comlis.view.fragment.CompanyEditDescriptionFragment

class CompanyEditDescriptionActivity: BaseActivity() {

    companion object {
        private val EXTRA_COMPANY_ID = "companyId"
        private val EXTRA_COLOR_NAME = "colorName"
        fun startForResult(fragment: Fragment, companyId: Int, colorName: String, requestCode: Int) {
            val intent = Intent(fragment.context, CompanyEditDescriptionActivity::class.java).apply {
                putExtra(EXTRA_COMPANY_ID, companyId)
                putExtra(EXTRA_COLOR_NAME, colorName)
            }
            fragment.startActivityForResult(intent, requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityCompanyEditDescriptionBinding>(this, R.layout.activity_company_edit_description)

        getComponent().inject(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            title = binding.toolbar.title
            setDisplayHomeAsUpEnabled(true)
        }

        val colorName = intent.getStringExtra(EXTRA_COLOR_NAME)
        binding.toolbar.background = ColorUtil.getImageCover(colorName, this)

        val companyId = intent.getIntExtra(EXTRA_COMPANY_ID, -1)
        replaceFragment(CompanyEditDescriptionFragment.create(companyId), R.id.content_view)
    }
}