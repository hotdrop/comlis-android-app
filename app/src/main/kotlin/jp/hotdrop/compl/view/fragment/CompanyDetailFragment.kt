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
import android.widget.ImageView
import com.google.android.flexbox.FlexboxLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CompanyDao
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
    lateinit var viewModel: CompanyDetailViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var companyDao: CompanyDao

    lateinit var binding: FragmentCompanyDetailBinding

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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyDetailBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        initToolbar()
        loadData()
        return binding.root
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(canPassResult(requestCode, resultCode) || data == null) {
            return
        }
        val refreshMode = data.getIntExtra(REFRESH_MODE, NONE)
        if(refreshMode != UPDATE && refreshMode != CHANGE_CATEGORY) {
            return
        }

        loadData()

        if(refreshMode == CHANGE_CATEGORY) {
            setResultForChangeCategory()
        } else {
            setResultForUpdate()
        }
        viewModel.closeFabMenu()
    }

    private fun canPassResult(requestCode: Int, resultCode: Int): Boolean {
        return (resultCode != Activity.RESULT_OK ||
                (requestCode != REQ_CODE_COMPANY_EDIT_OVERVIEW &&
                 requestCode != REQ_CODE_COMPANY_EDIT_INFORMATION &&
                 requestCode != REQ_CODE_COMPANY_EDIT_BUSINESS &&
                 requestCode != REQ_CODE_COMPANY_EDIT_DESCRIPTION &&
                 requestCode != REQ_CODE_COMPANY_ASSOCIATE_TAG))
    }

    private fun setResultForUpdate() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, UPDATE)
            putExtra(EXTRA_COMPANY_ID, viewModel.id)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        isRefresh = true
    }

    private fun setResultForTrash() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, DELETE)
            putExtra(EXTRA_COMPANY_ID, viewModel.id)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        isRefresh = true
    }

    private fun setResultForChangeCategory() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, CHANGE_CATEGORY)
            putExtra(EXTRA_COMPANY_ID, viewModel.id)
            putExtra(EXTRA_CATEGORY_NAME, viewModel.getCategoryName())
        }
        activity.setResult(Activity.RESULT_OK, intent)
        isRefresh = true
    }

    private fun loadData() {
        val disposable = viewModel.loadData(companyId, binding)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onSuccess() },
                        { throwable -> showErrorAsToast(ErrorType.LoadFailureCompany, throwable) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onSuccess() {

        fun setCardView(layout: FlexboxLayout, tag: Tag) {
            val binding = DataBindingUtil.inflate<ItemTagAssociateBinding>(getLayoutInflater(null),
                    R.layout.item_tag_associate, layout, false)
            binding.viewModel = TagAssociateViewModel(tag = tag, context = context, companyDao = companyDao)
            layout.addView(binding.root)
        }

        binding.viewModel = viewModel

        setImageCover(binding.imgCover, viewModel.colorName)

        val darkColor = ColorUtil.getResDark(viewModel.colorName, context)

        binding.imageEditAbstract.setColorFilter(darkColor)
        binding.imageEditInformation.setColorFilter(darkColor)
        binding.imageEditBusiness.setColorFilter(darkColor)
        binding.imageEditDescription.setColorFilter(darkColor)

        binding.fabDetailMenu.backgroundTintList = ColorStateList.valueOf(darkColor)
        binding.fabEdit.backgroundTintList = ColorStateList.valueOf(darkColor)
        binding.fabTag.backgroundTintList = ColorStateList.valueOf(darkColor)
        binding.fabTrash.backgroundTintList = ColorStateList.valueOf(darkColor)

        val flexBox = binding.flexBoxContainer
        flexBox.removeAllViews()
        viewModel.viewTags.forEach { tag -> setCardView(flexBox, tag) }

        initOnClickEvent()
        initFavoriteEvent()
    }

    private fun setImageCover(imageView: ImageView, colorName: String) {
        when(colorName) {
            ColorUtil.BLUE_NAME -> imageView.setImageResource(R.drawable.blue_cover)
            ColorUtil.GREEN_NAME -> imageView.setImageResource(R.drawable.green_cover)
            ColorUtil.RED_NAME -> imageView.setImageResource(R.drawable.red_cover)
            ColorUtil.YELLOW_NAME -> imageView.setImageResource(R.drawable.yellow_cover)
            ColorUtil.PURPLE_NAME -> imageView.setImageResource(R.drawable.purple_cover)
        }
    }

    private fun initOnClickEvent() {

        binding.nestedScrollView.setOnScrollChangeListener { _, _, _, _, _ ->
            if(!binding.fabDetailMenu.isShown && viewModel.isOpenFabMenu()) {
                viewModel.closeFabMenu()
            }
        }

        binding.fabDetailMenu.setOnClickListener {
            viewModel.onClickMenuFab()
        }

        binding.fabEdit.setOnClickListener {
            viewModel.onClickModeEditFab()
        }

        binding.fabTag.setOnClickListener {
            ActivityNavigator.showCompanyAssociateTag(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_ASSOCIATE_TAG)
        }

        binding.fabTrash.setOnClickListener {
            val dialog = AlertDialog.Builder(context, R.style.DialogTheme)
                    .setMessage(R.string.detail_dialog_trash_button_message)
                    .setPositiveButton(R.string.dialog_ok, {dialogInterface, _ ->
                        viewModel.delete()
                        dialogInterface.dismiss()
                        setResultForTrash()
                        exit()
                    })
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .create()
            dialog.show()
        }

        binding.toolbarLayout.setOnClickListener {
            // タイトルをタップ時に編集モードであれば概要と同じ画面に遷移する
            ActivityNavigator.showCompanyEditOverview(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_EDIT_OVERVIEW)
        }
        binding.toolbarLayout.isClickable = false

        binding.imageEditAbstract.setOnClickListener {
            ActivityNavigator.showCompanyEditOverview(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_EDIT_OVERVIEW)
        }

        binding.imageEditInformation.setOnClickListener {
            ActivityNavigator.showCompanyEditInfo(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_EDIT_INFORMATION)
        }

        binding.imageEditBusiness.setOnClickListener {
            ActivityNavigator.showCompanyEditBusiness(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_EDIT_BUSINESS)
        }

        binding.imageEditDescription.setOnClickListener {
            ActivityNavigator.showCompanyEditDescription(this@CompanyDetailFragment, companyId,
                    viewModel.colorName, REQ_CODE_COMPANY_EDIT_DESCRIPTION)
        }

    }

    private fun initFavoriteEvent() {

        // スターつけて一覧画面に戻った時にスターの状態を一覧に反映したいが、その通知契機がないのでここで対応する。
        // スター2つ→3つつける→やっぱ2つに戻す、としても変更した時点でsetIntentが走っているので元のスター数に戻しても
        // 画面更新される。
        fun changedFavorite() {
            if(viewModel.isEditFavorite() && !isRefresh) {
                setResultForUpdate()
            }
        }

        val animView1 = binding.animationView1.apply {
            setFavoriteStar()
            setOnClickListener {
                viewModel.onClickFirstFavorite()
                changedFavorite()
            }
        }
        val animView2 = binding.animationView2.apply {
            setFavoriteStar()
            setOnClickListener {
                viewModel.onClickSecondFavorite()
                changedFavorite()
            }
        }
        val animView3 = binding.animationView3.apply {
            setFavoriteStar()
            setOnClickListener {
                viewModel.onClickThirdFavorite()
                changedFavorite()
            }
        }
        mutableListOf(animView1, animView2, animView3).take(viewModel.viewFavorite).forEach { it.playAnimation() }
    }
}