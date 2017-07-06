package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
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

    private fun AppCompatEditText.createEmptyOrNumberObservable(): Observable<Boolean> {
        return RxTextView
                .textChangeEvents(this)
                .map {
                    it.text().isEmpty() || it.text().isNumber()
                }.distinctUntilChanged()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyEditInfoBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        binding.viewModel = viewModel

        viewModel.loadData(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onComplete = { binding.viewModel = viewModel },
                        onError = { showErrorAsToast(ErrorType.LoadFailureCompany, it) }
                )
                .addTo(compositeDisposable)

        val employeeNumObservable = binding.txtEmployeesNum.createEmptyOrNumberObservable()
        val salaryLowObservable = binding.txtSalaryLower.createEmptyOrNumberObservable()
        val salaryHighObservable = binding.txtSalaryHigh.createEmptyOrNumberObservable()

        employeeNumObservable
                .subscribeBy(
                        onNext = { binding.labelEmployeesNumAttention.visibility = if (it) View.GONE else View.VISIBLE }
                )
                .addTo(compositeDisposable)

        Observables
                .combineLatest(salaryLowObservable, salaryHighObservable, { isLow, isHigh -> isLow && isHigh})
                .subscribeBy(
                        onNext = { binding.labelSalaryAttention.visibility = if(it) View.GONE else View.VISIBLE }
                )
                .addTo(compositeDisposable)

        Observables
                .combineLatest(employeeNumObservable, salaryLowObservable, salaryHighObservable,
                    { isEmployee, isLow, isHigh -> isEmployee && isLow && isHigh })
                .subscribeBy( onNext = {
                    if(it) binding.updateButton.enabledWithColor(viewModel.getColorRes()) else binding.updateButton.disabledWithColor()
                })
                .addTo(compositeDisposable)

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