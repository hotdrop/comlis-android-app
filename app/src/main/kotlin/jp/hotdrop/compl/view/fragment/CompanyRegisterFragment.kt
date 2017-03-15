package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import jp.hotdrop.compl.databinding.FragmentCompanyRegisterBinding
import jp.hotdrop.compl.util.AppCode
import jp.hotdrop.compl.view.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyRegisterViewModel

class CompanyRegisterFragment : BaseFragment() {

    private lateinit var categorySpinner: CategorySpinner
    private lateinit var binding: FragmentCompanyRegisterBinding
    private lateinit var viewModel: CompanyRegisterViewModel

    companion object {
        fun create() = CompanyRegisterFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity)
        binding.registerButton.setOnClickListener { onClickRegister() }
        viewModel = CompanyRegisterViewModel()
        binding.companyViewModel = viewModel
        return binding.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }

    private fun onClickRegister() {
        val returnCode = viewModel.register(categorySpinner.getSelection())
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
            putExtra(REFRESH_MODE, REFRESH_ALL)
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
