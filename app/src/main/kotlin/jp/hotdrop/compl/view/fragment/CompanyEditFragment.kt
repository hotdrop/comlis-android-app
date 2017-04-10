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
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyEditViewModel

class CompanyEditFragment: BaseFragment() {

    private lateinit var binding: FragmentCompanyEditBinding
    private lateinit var viewModel: CompanyEditViewModel
    private lateinit var categorySpinner: CategorySpinner

    private val companyId by lazy {
        arguments.getInt(EXTRA_COMPANY_ID)
    }

    private val colorName by lazy {
        arguments.getString(EXTRA_COLOR_NAME)
    }

    companion object {
        @JvmStatic private val EXTRA_COMPANY_ID = "companyId"
        @JvmStatic private val EXTRA_COLOR_NAME = "colorName"
        fun create(companyId: Int, colorName: String) = CompanyEditFragment().apply {
            arguments = Bundle().apply {
                putInt(EXTRA_COMPANY_ID, companyId)
                putString(EXTRA_COLOR_NAME, colorName)
            }
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

        viewModel = CompanyEditViewModel(companyId, context)
        binding.viewModel = viewModel
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity).apply {
            setSelection(viewModel.categoryName)
        }

        binding.updateButton.setOnClickListener{ onClickUpdate() }

        return binding.root
    }

    private fun onClickUpdate() {
        val errorMessage = viewModel.update(categorySpinner.getSelection())
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