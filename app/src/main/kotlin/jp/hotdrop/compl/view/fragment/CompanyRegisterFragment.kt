package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.databinding.FragmentCompanyRegisterBinding
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyRegisterViewModel
import javax.inject.Inject

class CompanyRegisterFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: CompanyRegisterViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var categoryDao: CategoryDao

    private lateinit var categorySpinner: CategorySpinner
    private lateinit var binding: FragmentCompanyRegisterBinding
    private val selectedCategoryName by lazy { arguments.getString(EXTRA_CATEGORY_NAME) }

    companion object {
        fun create(tabName: String?) = CompanyRegisterFragment().apply {
            arguments = Bundle().apply { putString(EXTRA_CATEGORY_NAME, tabName) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }
    
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity, categoryDao).apply {
            setSelection(selectedCategoryName)
        }

        val nameObservable = RxTextView.afterTextChangeEvents(binding.txtName).map { it.editable().toString() }.distinctUntilChanged()
        val employeeNumObservable = RxTextView.afterTextChangeEvents(binding.txtEmployeesNum).map { viewModel.checkNumber(it.editable().toString()) }.distinctUntilChanged()
        val salaryLowObservable = RxTextView.afterTextChangeEvents(binding.txtSalaryLower).map { viewModel.checkNumber(it.editable().toString()) }.distinctUntilChanged()
        val salaryHighObservable = RxTextView.afterTextChangeEvents(binding.txtSalaryHigh).map { viewModel.checkNumber(it.editable().toString()) }.distinctUntilChanged()

        val disposable1 = nameObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ name ->
                    if(name.isNullOrEmpty()) {
                        binding.labelNameAttention.text = context.getString(R.string.company_name_empty_attention)
                        binding.labelNameAttention.visibility = View.VISIBLE
                    } else if(viewModel.existName(name)) {
                        binding.labelNameAttention.text = context.getString(R.string.company_name_attention)
                        binding.labelNameAttention.visibility = View.VISIBLE
                    } else {
                        binding.labelNameAttention.visibility = View.GONE
                    }
                }
        compositeDisposable.add(disposable1)

        val disposable2 = employeeNumObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ isNumber ->
                    binding.labelEmployeesNumAttention.visibility = if (isNumber) View.GONE else View.VISIBLE
                }
        compositeDisposable.add(disposable2)

        val salaryCombined: Observable<Boolean> = Observable.combineLatest(
                salaryLowObservable,
                salaryHighObservable,
                BiFunction { low: Boolean, high: Boolean -> low && high })

        val disposable3 = salaryCombined
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    binding.labelSalaryAttention.visibility = if (result) View.GONE else View.VISIBLE
                }
        compositeDisposable.add(disposable3)

        val buttonObservable: Observable<Boolean> = Observable.combineLatest(
                nameObservable,
                employeeNumObservable,
                salaryCombined,
                Function3 { name: String, isNumEmployee: Boolean, isNumSalary: Boolean ->
                    ( !name.isNullOrEmpty() && binding.labelNameAttention.visibility == View.GONE && isNumEmployee && isNumSalary )
                })
        val disposable4 = buttonObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ binding.registerButton.isEnabled = it }
        compositeDisposable.add(disposable4)

        binding.registerButton.setOnClickListener { onClickRegister() }
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }

    private fun onClickRegister() {
        viewModel.register(categorySpinner.getSelection())
        val intent = Intent().apply {
            val categoryName = viewModel.getCategoryName(categorySpinner.getSelection())
            putExtra(EXTRA_CATEGORY_NAME, categoryName)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }
}
