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
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.databinding.FragmentCompanyEditBinding
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyEditViewModel
import javax.inject.Inject

class CompanyEditFragment: BaseFragment() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    private lateinit var binding: FragmentCompanyEditBinding
    private lateinit var viewModel: CompanyEditViewModel
    private lateinit var categorySpinner: CategorySpinner

    private val companyId by lazy {
        arguments.getInt(EXTRA_COMPANY_ID)
    }

    companion object {
        fun create(companyId: Int) = CompanyEditFragment().apply {
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
        binding = FragmentCompanyEditBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)

        viewModel = CompanyEditViewModel(companyId, context, companyDao, categoryDao)
        binding.viewModel = viewModel
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity, categoryDao).apply {
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
            putExtra(REFRESH_MODE, UPDATE)
        }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }
}