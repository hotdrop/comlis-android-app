package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.databinding.FragmentCompanyEditOverviewBinding
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyEditOverviewViewModel
import javax.inject.Inject

class CompanyEditOverviewFragment: BaseFragment() {

    @Inject
    lateinit var viewModel: CompanyEditOverviewViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var categoryDao: CategoryDao

    private lateinit var binding: FragmentCompanyEditOverviewBinding
    private lateinit var categorySpinner: CategorySpinner
    private val companyId by lazy { arguments.getInt(EXTRA_COMPANY_ID) }

    companion object {
        fun create(companyId: Int) = CompanyEditOverviewFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_COMPANY_ID, companyId)
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyEditOverviewBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        binding.viewModel = viewModel
        val disposable = viewModel.loadData(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { onLoadSuccess() },
                    { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable)

        binding.updateButton.setOnClickListener{ onClickUpdate() }
        return binding.root
    }

    private fun onLoadFailure(e: Throwable) {
        Toast.makeText(activity, "failed load companies." + e.message, Toast.LENGTH_LONG).show()
    }

    private fun onLoadSuccess() {
        binding.viewModel = viewModel
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity, categoryDao).apply {
            setSelection(viewModel.categoryId)
        }
    }

    private fun onClickUpdate() {
        val errorMessage = viewModel.update(categorySpinner.getSelection())
        if(errorMessage == null) {
            onSuccess()
        } else {
            Toast.makeText(context, errorMessage.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun onSuccess() {
        val intent = Intent().apply {
            if(viewModel.isChangeCategory) putExtra(REFRESH_MODE, CHANGE_CATEGORY) else putExtra(REFRESH_MODE, UPDATE)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }
}