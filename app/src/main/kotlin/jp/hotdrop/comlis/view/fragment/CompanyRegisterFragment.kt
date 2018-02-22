package jp.hotdrop.comlis.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.AppCompatEditText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import jp.hotdrop.comlis.R
import jp.hotdrop.comlis.databinding.FragmentCompanyRegisterBinding
import jp.hotdrop.comlis.repository.category.CategoryRepository
import jp.hotdrop.comlis.view.parts.CategorySpinner
import jp.hotdrop.comlis.viewmodel.CompanyRegisterViewModel
import javax.inject.Inject

class CompanyRegisterFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: CompanyRegisterViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var categoryRepository: CategoryRepository

    private lateinit var categorySpinner: CategorySpinner
    private lateinit var binding: FragmentCompanyRegisterBinding
    private val selectedCategoryName by lazy { arguments!!.getString(EXTRA_CATEGORY_NAME) }

    companion object {
        fun create(tabName: String?) = CompanyRegisterFragment().apply {
            arguments = Bundle().apply { putString(EXTRA_CATEGORY_NAME, tabName) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        binding.viewModel = viewModel

        createObservableToEditTexts()

        categorySpinner = CategorySpinner(binding.spinnerCategory, activity, categoryRepository).apply {
            setSelection(selectedCategoryName)
        }

        // createObservableToEditTextsで設定したObservableはsubscribe（ユーザーが何か入力）しないと動かないので
        // 初期状態ではボタンを非活性にしておく。
        binding.registerButton.isEnabled = false
        binding.registerButton.setOnClickListener { onClickRegister() }

        return binding.root
    }

    private fun AppCompatEditText.createNotExistObservable() =
            RxTextView.textChangeEvents(this)
                    .filter { it.text().toString().isNotBlank() }
                    .map { !viewModel.existName(it.text().toString()) }
                    .distinctUntilChanged()

    private fun createObservableToEditTexts() {

        val nameNotBlankObservable = binding.txtName.createNotBlankObservable()
        val nameNotExistObservable = binding.txtName.createNotExistObservable()

        Observables.combineLatest(nameNotBlankObservable, nameNotExistObservable,
                { isNotBlack, isNotExist ->
                    when {
                        !isNotBlack -> setLabelNameAttentionToEmptyMessage()
                        !isNotExist -> setLabelNameAttentionToExistMessage()
                    }
                    (isNotBlack && isNotExist)
                })
                .subscribeBy(
                        onNext = {viewNameAttention(it)}
                )
                .addTo(compositeDisposable)

        val employeeNumObservable = binding.txtEmployeesNum.createEmptyOrNumberObservable()
        employeeNumObservable
                .subscribeBy(
                        onNext = {viewEmployeesNumAttention(it)}
                )
                .addTo(compositeDisposable)

        val salaryLowObservable = binding.txtSalaryLower.createEmptyOrNumberObservable()
        val salaryHighObservable = binding.txtSalaryHigh.createEmptyOrNumberObservable()
        Observables.combineLatest(salaryLowObservable, salaryHighObservable,
                    { isLow, isHigh -> isLow && isHigh })
                .subscribeBy(
                        onNext = { viewSalaryAttention(it) }
                )
                .addTo(compositeDisposable)

        // combineFunctionの引数は、スコープが短いことと各々のBoolean値を識別する必要がないため1文字にする
        Observables.combineLatest(nameNotBlankObservable, nameNotExistObservable,
                        employeeNumObservable,
                        salaryLowObservable, salaryHighObservable,
                        { a, b, c, d, e -> ( a && b && c && d && e ) })
                .subscribeBy(
                        onNext = {binding.registerButton.isEnabled = it}
                )
                .addTo(compositeDisposable)
    }

    private fun setLabelNameAttentionToEmptyMessage() {
        context?.let {
            binding.labelNameAttention.text = it.getString(R.string.company_name_empty_attention)
        }
    }

    private fun setLabelNameAttentionToExistMessage() {
        context?.let {
            binding.labelNameAttention.text = it.getString(R.string.company_name_attention)
        }
    }

    private fun viewNameAttention(gone: Boolean) {
        binding.labelNameAttention.visibility = if (gone) View.GONE else View.VISIBLE
    }

    private fun viewEmployeesNumAttention(gone: Boolean) {
        binding.labelEmployeesNumAttention.visibility = if(gone) View.GONE else View.VISIBLE
    }

    private fun viewSalaryAttention(gone: Boolean) {
        binding.labelSalaryAttention.visibility = if(gone) View.GONE else View.VISIBLE
    }

    private fun onClickRegister() {
        viewModel.register(categorySpinner.getSelection())

        activity?.run {
            val intent = Intent().apply {
                val categoryName = viewModel.getCategoryName(categorySpinner.getSelection())
                putExtra(EXTRA_CATEGORY_NAME, categoryName)
            }
            this.setResult(Activity.RESULT_OK, intent)
        }

        exit()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
