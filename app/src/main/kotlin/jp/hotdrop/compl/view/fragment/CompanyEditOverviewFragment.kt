package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.R
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

        val disposable1 = viewModel.loadData(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                    { onLoadSuccess() },
                    { throwable -> onLoadFailure(throwable) }
                )
        compositeDisposable.add(disposable1)

        val nameEmptyObservable = RxTextView.textChangeEvents(binding.txtName)
                .map { it.text().isBlank() }
                .distinctUntilChanged()
        val nameDuplicateObservable = RxTextView.textChangeEvents(binding.txtName)
                .map { viewModel.existName(it.text().toString()) }
                .distinctUntilChanged()
        val nameObservableCombined: Observable<Boolean> = Observable.combineLatest(
                nameEmptyObservable,
                nameDuplicateObservable,
                BiFunction { isEmpty: Boolean, isDuplicate: Boolean ->
                    when {
                        isEmpty -> binding.labelNameAttention.text = context.getString(R.string.company_name_empty_attention)
                        isDuplicate -> binding.labelNameAttention.text = context.getString(R.string.company_name_attention)
                    }
                    (!isEmpty && !isDuplicate)
                })
        val disposable2 = nameObservableCombined
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    if (result) {
                        binding.labelNameAttention.visibility = View.GONE
                        binding.updateButton.enabledWithColor(viewModel.getColorRes())
                    } else {
                        binding.labelNameAttention.visibility = View.VISIBLE
                        binding.updateButton.disabledWithColor()
                    }
                }
        compositeDisposable.add(disposable2)

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
        viewModel.update(categorySpinner.getSelection())
        val intent = Intent().apply {
            if(viewModel.isChangeCategory) putExtra(REFRESH_MODE, CHANGE_CATEGORY) else putExtra(REFRESH_MODE, UPDATE)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }
}