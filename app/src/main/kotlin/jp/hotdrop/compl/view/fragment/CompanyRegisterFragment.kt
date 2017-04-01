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
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyRegisterViewModel

class CompanyRegisterFragment : BaseFragment() {

    private lateinit var categorySpinner: CategorySpinner
    private lateinit var binding: FragmentCompanyRegisterBinding
    private lateinit var viewModel: CompanyRegisterViewModel
    private lateinit var selectedTabName: String

    companion object {
        @JvmStatic val EXTRA_TAB_NAME = "tabName"
        fun create(tabName: String) = CompanyRegisterFragment().apply {
            arguments = Bundle().apply { putString(EXTRA_TAB_NAME, tabName) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedTabName = arguments.getString(EXTRA_TAB_NAME)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity).apply {
            setSelection(selectedTabName)
        }
        binding.registerButton.setOnClickListener { onClickRegister() }
        viewModel = CompanyRegisterViewModel(context)
        binding.viewModel = viewModel
        return binding.root
    }

    private fun onClickRegister() {
        val errorMessage = viewModel.register(categorySpinner.getSelection())
        if(errorMessage == null) {
            onSuccess()
        } else {
            Toast.makeText(context, errorMessage.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun onSuccess() {
        val intent = Intent().apply {
            putExtra(REFRESH_MODE, REFRESH)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }
}
