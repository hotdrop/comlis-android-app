package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.lottie.LottieAnimationView
import com.google.android.flexbox.FlexboxLayout
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.databinding.FragmentCompanyDetailBinding
import jp.hotdrop.compl.databinding.ItemTagAssociateBinding
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil
import jp.hotdrop.compl.view.activity.ActivityNavigator
import jp.hotdrop.compl.viewmodel.CompanyDetailViewModel
import jp.hotdrop.compl.viewmodel.TagAssociateViewModel
import javax.inject.Inject

class CompanyDetailFragment: BaseFragment() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var tagDao: TagDao

    lateinit var binding: FragmentCompanyDetailBinding
    private lateinit var viewModel: CompanyDetailViewModel
    private var isRefresh = false

    private val companyId by lazy { arguments.getInt(EXTRA_COMPANY_ID) }

    companion object {
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
        if(resultCode != Activity.RESULT_OK ||
                (requestCode != REQ_CODE_COMPANY_EDIT && requestCode != REQ_CODE_COMPANY_ASSOCIATE_TAG) ||
                data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, NONE)
        if(refreshMode != UPDATE) {
            return
        }

        val beforeCategoryId = viewModel.company.categoryId
        refreshLayout()
        val afterCategoryId = viewModel.company.categoryId

        if(beforeCategoryId == afterCategoryId) {
            setResultForUpdate()
        } else {
            setResultForChangeCategory()
        }
    }

    private fun setResultForUpdate() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, UPDATE)
            putExtra(EXTRA_COMPANY_ID, viewModel.company.id)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        isRefresh = true
    }

    private fun setResultForTrash() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, DELETE)
            putExtra(EXTRA_COMPANY_ID, viewModel.company.id)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        isRefresh = true
    }

    private fun setResultForChangeCategory() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, CHANGE_CATEGORY)
            putExtra(EXTRA_COMPANY_ID, viewModel.company.id)
            putExtra(EXTRA_CATEGORY_NAME, viewModel.getCategoryName())
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
        val animView1 = binding.animationView1.apply { setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak) }
        val animView2 = binding.animationView2.apply { setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak) }
        val animView3 = binding.animationView3.apply { setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak) }
        val animViews = mutableListOf(animView1, animView2, animView3)

        animViews.take(viewModel.viewFavorite).forEach { it.playAnimation() }

        // スターつけて一覧画面に戻った時にスターの状態を一覧に反映したいが、その通知契機がないのでここで対応する。
        // スター2つ→3つつける→やっぱ2つに戻す、としても変更した時点でsetIntentが走っているので元のスター数に戻しても
        // 画面更新される。
        fun changedFavorite() {
            if(viewModel.company.favorite != viewModel.viewFavorite && !isRefresh) {
                setResultForUpdate()
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
            if(viewModel.isThreeFavorite()) {
                animViews.forEach { it.progress = RESET }
                viewModel.resetFavorite()
            } else {
                animViews.forEach { it.playAnimation() }
                viewModel.tapFavorite(3)
            }
            changedFavorite()
        }

        binding.fabEdit.setOnClickListener {
            ActivityNavigator.showCompanyEdit(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_EDIT)
        }

        binding.fabTag.setOnClickListener {
            ActivityNavigator.showCompanyAssociateTag(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_ASSOCIATE_TAG)
        }

        binding.fabTrash.setOnClickListener {
            val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                    .setMessage(R.string.detail_dialog_trash_button_message)
                    .setPositiveButton(R.string.dialog_ok, {dialogInterface, _ ->
                        companyDao.delete(viewModel.company)
                        dialogInterface.dismiss()
                        setResultForTrash()
                        exit()
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .create()
            dialog.show()
        }
    }

    private fun refreshLayout() {
        viewModel = CompanyDetailViewModel(companyId, context, binding, companyDao, categoryDao, tagDao)
        binding.viewModel = viewModel
        binding.fabEdit.backgroundTintList = ColorStateList.valueOf(ColorUtil.getResDark(viewModel.colorName, context))
        binding.fabTag.backgroundTintList = ColorStateList.valueOf(ColorUtil.getResDark(viewModel.colorName, context))
        binding.fabTrash.backgroundTintList = ColorStateList.valueOf(ColorUtil.getResDark(viewModel.colorName, context))

        val flexbox = binding.flexBoxContainer
        flexbox.removeAllViews()
        viewModel.viewTags.forEach { tag -> setCardView(flexbox, tag) }
    }

    private fun setCardView(flexboxLayout: FlexboxLayout, tag: Tag) {
        val binding = DataBindingUtil.inflate<ItemTagAssociateBinding>(getLayoutInflater(null),
                R.layout.item_tag_associate, flexboxLayout, false)
        binding.viewModel = TagAssociateViewModel(tag = tag, context = context, companyDao = companyDao)
        flexboxLayout.addView(binding.root)
    }
}