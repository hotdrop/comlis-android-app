package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.databinding.FragmentCompanyRegisterBinding
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyRegisterViewModel
import javax.inject.Inject

class CompanyRegisterFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: CompanyRegisterViewModel
    @Inject
    lateinit var categoryDao: CategoryDao

    private lateinit var categorySpinner: CategorySpinner
    private lateinit var binding: FragmentCompanyRegisterBinding
    private var selectedCategoryName: String? = null

    companion object {
        fun create(tabName: String?) = CompanyRegisterFragment().apply {
            arguments = Bundle().apply { putString(EXTRA_CATEGORY_NAME, tabName) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectedCategoryName = arguments.getString(EXTRA_CATEGORY_NAME)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity, categoryDao).apply {
            setSelection(selectedCategoryName)
        }
        binding.registerButton.setOnClickListener { onClickRegister() }
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
            val categoryName = viewModel.getCategoryName(categorySpinner.getSelection())
            putExtra(EXTRA_CATEGORY_NAME, categoryName)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }
}
