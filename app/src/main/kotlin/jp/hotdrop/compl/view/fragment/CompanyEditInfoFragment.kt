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
import jp.hotdrop.compl.databinding.FragmentCompanyEditInfoBinding
import jp.hotdrop.compl.viewmodel.CompanyEditInfoViewModel
import javax.inject.Inject

class CompanyEditInfoFragment: BaseFragment() {

    @Inject
    lateinit var viewModel: CompanyEditInfoViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var binding: FragmentCompanyEditInfoBinding
    private val companyId by lazy { arguments.getInt(EXTRA_COMPANY_ID) }

    companion object {
        fun create(companyId: Int) = CompanyEditInfoFragment().apply {
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
        binding = FragmentCompanyEditInfoBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        binding.viewModel = viewModel

        val disposable = viewModel.loadData(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { binding.viewModel = viewModel },
                        { e -> Toast.makeText(activity, "failed load companies." + e.message, Toast.LENGTH_LONG).show()}
                )
        compositeDisposable.add(disposable)

        val employeeNumObservable = RxTextView.textChangeEvents(binding.txtEmployeesNum)
                .map { viewModel.isAllNumbers(it.text().toString()) }
                .distinctUntilChanged()
        val salaryLowObservable = RxTextView.textChangeEvents(binding.txtSalaryLower)
                .map { viewModel.isAllNumbers(it.text().toString()) }
                .distinctUntilChanged()
        val salaryHighObservable = RxTextView.textChangeEvents(binding.txtSalaryHigh)
                .map { viewModel.isAllNumbers(it.text().toString()) }
                .distinctUntilChanged()

        val disposable1 = employeeNumObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ isNumber ->
                    binding.labelEmployeesNumAttention.visibility = if (isNumber) View.GONE else View.VISIBLE
                }
        compositeDisposable.add(disposable1)

        val salaryCombined: Observable<Boolean> = Observable.combineLatest(
                salaryLowObservable,
                salaryHighObservable,
                BiFunction { low: Boolean, high: Boolean -> low && high })
        val disposable2 = salaryCombined
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { result ->
                    binding.labelSalaryAttention.visibility = if (result) View.GONE else View.VISIBLE
                }
        compositeDisposable.add(disposable2)

        val buttonObservable: Observable<Boolean> = Observable.combineLatest(
                employeeNumObservable,
                salaryCombined,
                BiFunction { isEmployeeValidate: Boolean, isSalaryValidate: Boolean -> ( isEmployeeValidate && isSalaryValidate ) })
        val disposable3 = buttonObservable
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ enabled ->
                    if(enabled) {
                        binding.updateButton.enabledWithColor(viewModel.getColorRes())
                    } else {
                        binding.updateButton.disabledWithColor()
                    }
                }
        compositeDisposable.add(disposable3)

        binding.updateButton.setOnClickListener{ onClickUpdate() }
        return binding.root
    }

    private fun onClickUpdate() {
        viewModel.update()
        val intent = Intent().apply { putExtra(REFRESH_MODE, UPDATE) }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }
}