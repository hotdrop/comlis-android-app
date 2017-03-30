package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.hotdrop.compl.databinding.FragmentCompanyDetailBinding
import jp.hotdrop.compl.util.ColorUtil
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.viewmodel.CompanyDetailViewModel

class CompanyDetailFragment: BaseFragment() {

    private lateinit var binding: FragmentCompanyDetailBinding
    private lateinit var viewModel: CompanyDetailViewModel
    private var isRefresh = false

    private val companyId by lazy {
        arguments.getInt(EXTRA_COMPANY_ID)
    }

    companion object {
        @JvmStatic val EXTRA_COMPANY_ID = "companyId"
        fun create(companyId: Int) = CompanyDetailFragment().apply {
            arguments = Bundle().apply { putInt(EXTRA_COMPANY_ID, companyId) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        initToolbar()
        initLayout()
        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode != Activity.RESULT_OK || requestCode != REQ_CODE_COMPANY_EDIT || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, REFRESH_NONE)
        if(refreshMode == REFRESH) {
            refreshLayout()
            setResult()
        }
    }

    private fun setResult() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, REFRESH)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        isRefresh = true
    }

    private fun initToolbar() {
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeButtonEnabled(true)
        }
    }

    private val RESET = 0.toFloat()
    private fun initLayout() {
        refreshLayout()
        val animView1 = binding.animationView1.apply { setAnimation("FavoriteStar.json") }
        val animView2 = binding.animationView2.apply { setAnimation("FavoriteStar.json") }
        val animView3 = binding.animationView3.apply { setAnimation("FavoriteStar.json") }
        val animViews = mutableListOf(animView1, animView2, animView3)

        animViews.take(viewModel.viewFavorite).forEach { it.playAnimation() }

        // スターつけて一覧画面に戻った時にスターの状態を一覧に反映したいが、その通知契機がないのでここで対応する。
        // スター2つ→3つつける→やっぱ2つに戻す、としても変更した時点でsetIntentが走っているので元のスター数に戻しても
        // 画面更新される。
        fun changedFavorite() {
            if(viewModel.company.favorite != viewModel.viewFavorite && !isRefresh) {
                setResult()
            }
        }

        animView1.setOnClickListener {
            if(viewModel.isOneFavorite()) {
                animView1.progress = RESET
                viewModel.resetFavorite()
            } else {
                animView1.playAnimation()
                animView2.progress = RESET
                animView3.progress = RESET
                viewModel.tapFavorite(1)
            }
            changedFavorite()
        }
        animView2.setOnClickListener {
            if(viewModel.isTwoFavorite()) {
                animView1.progress = RESET
                animView2.progress = RESET
                viewModel.resetFavorite()
            } else {
                animView1.playAnimation()
                animView2.playAnimation()
                animView3.progress = RESET
                viewModel.tapFavorite(2)
            }
            changedFavorite()
        }
        animView3.setOnClickListener {
            if(viewModel.isThreeFacorite()) {
                animViews.forEach { it.progress = RESET }
                viewModel.resetFavorite()
            } else {
                animViews.forEach { it.playAnimation() }
                viewModel.tapFavorite(3)
            }
            changedFavorite()
        }

        binding.fab.setOnClickListener {
            ActivityNavigator.showCompanyEdit(this@CompanyDetailFragment, companyId, REQ_CODE_COMPANY_EDIT)
        }
    }

    private fun refreshLayout() {
        viewModel = CompanyDetailViewModel(companyId, context)
        binding.viewModel = viewModel
        binding.fab.backgroundTintList = ColorStateList.valueOf(ColorUtil.getResNormal(viewModel.colorName, context))
    }
}