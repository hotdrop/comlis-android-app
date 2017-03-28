package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import jp.hotdrop.compl.databinding.FragmentCompanyEditBinding
import jp.hotdrop.compl.util.AppCode
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyEditViewModel

class CompanyEditFragment: BaseFragment() {

    private lateinit var binding: FragmentCompanyEditBinding
    private lateinit var viewModel: CompanyEditViewModel
    private lateinit var categorySpinner: CategorySpinner

    private val companyId by lazy {
        arguments.getInt(EXTRA_COMPANY_ID)
    }

    companion object {
        @JvmStatic val EXTRA_COMPANY_ID = "companyId"
        fun create(companyId: Int) = CompanyEditFragment().apply {
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
        binding = FragmentCompanyEditBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        viewModel = CompanyEditViewModel(companyId)
        binding.viewModel = viewModel
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity).apply {
            setSelection(viewModel.categoryName)
        }
        binding.updateButton.setOnClickListener{ onClickUpdate() }
        return binding.root
    }

    private fun onClickUpdate() {
        val returnCode = viewModel.update(categorySpinner.getSelection())
        when(returnCode) {
            AppCode.ERROR_EMPTY_COMPANY_NAME -> showToast("会社名を入力してください。")
            AppCode.ERROR_NOT_NUMBER_EMPLOYEES_NUM -> showToast("従業員は数値を入力してください。")
            AppCode.ERROR_NOT_NUMBER_SALARY -> showToast("年収は数値を入力してください。")
            AppCode.OK -> onSuccess()
        }
    }

    private fun showToast(messages: String) {
        Toast.makeText(context, messages, Toast.LENGTH_LONG).show()
    }

    private fun onSuccess() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, REFRESH)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }

    private fun exit() {
        if(isResumed) {
            activity.onBackPressed()
        }
    }
}