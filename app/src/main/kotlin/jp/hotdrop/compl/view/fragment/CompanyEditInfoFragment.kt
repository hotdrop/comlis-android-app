package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyEditInfoBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        binding.viewModel = viewModel

        viewModel.loadData(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onComplete = {
                            createObservableToEditTexts()
                            binding.updateButton.setOnClickListener{ onClickUpdate() }
                        },
                        onError = { showErrorAsToast(ErrorType.LoadFailureCompany, it) }
                )
                .addTo(compositeDisposable)

        return binding.root
    }

    private fun createObservableToEditTexts() {

        val employeeNumObservable = binding.txtEmployeesNum.createEmptyOrNumberObservable()
        val salaryLowObservable = binding.txtSalaryLower.createEmptyOrNumberObservable()
        val salaryHighObservable = binding.txtSalaryHigh.createEmptyOrNumberObservable()

        // PublisherがUIスレッドで実行されるのでObserveOnでのAndroidのmainThreadは指定しない。
        // 本当は暗黙にすると怖いので指定すべきかもしれない・・
        employeeNumObservable
                .subscribeBy(
                        onNext = { viewEmployeesNumAttention(it) }
                )
                .addTo(compositeDisposable)

        Observables.combineLatest(salaryLowObservable, salaryHighObservable,
                    { isLow, isHigh -> isLow && isHigh})
                .subscribeBy(
                        onNext = { viewSalaryAttention(it) }
                )
                .addTo(compositeDisposable)

        // combineFunctionの引数は、スコープが短いことと各々のBoolean値を識別する必要がないため1文字にする
        Observables.combineLatest(employeeNumObservable, salaryLowObservable, salaryHighObservable,
                    { a, b, c -> a && b && c })
                .subscribeBy(
                        onNext = { enableOrDisableUpdateButton(it) }
                )
                .addTo(compositeDisposable)
    }

    private fun viewEmployeesNumAttention(gone: Boolean) {
        binding.labelEmployeesNumAttention.visibility = if(gone) View.GONE else View.VISIBLE
    }

    private fun viewSalaryAttention(gone: Boolean) {
        binding.labelSalaryAttention.visibility = if(gone) View.GONE else View.VISIBLE
    }

    private fun enableOrDisableUpdateButton(enable: Boolean) {
        if(enable)
            binding.updateButton.enabledWithColor(viewModel.getColorRes())
        else
            binding.updateButton.disabledWithColor()
    }

    private fun onClickUpdate() {
        viewModel.update()

        val intent = Intent().apply { putExtra(REFRESH_MODE, UPDATE) }
        activity.setResult(Activity.RESULT_OK, intent)

        exit()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}