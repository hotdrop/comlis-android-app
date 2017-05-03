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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.databinding.FragmentCompanyRegisterBinding
import jp.hotdrop.compl.view.parts.CategorySpinner
import jp.hotdrop.compl.viewmodel.CompanyRegisterViewModel
import javax.inject.Inject

class CompanyRegisterFragment : BaseFragment() {

    @Inject
    lateinit var viewModel: CompanyRegisterViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var categoryDao: CategoryDao

    private lateinit var categorySpinner: CategorySpinner
    private lateinit var binding: FragmentCompanyRegisterBinding
    private val selectedCategoryName by lazy { arguments.getString(EXTRA_CATEGORY_NAME) }

    companion object {
        fun create(tabName: String?) = CompanyRegisterFragment().apply {
            arguments = Bundle().apply { putString(EXTRA_CATEGORY_NAME, tabName) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        getComponent().inject(this)
    }
    
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCompanyRegisterBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        categorySpinner = CategorySpinner(binding.spinnerCategory, activity, categoryDao).apply {
            setSelection(selectedCategoryName)
        }

        val observable = RxTextView.afterTextChangeEvents(binding.txtName)
                .map { viewModel.existName(it.editable().toString()) }
                .distinctUntilChanged()
        val disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{ duplicate ->
                    if(duplicate) {
                        binding.labelNameAttention.visibility = View.VISIBLE
                        binding.registerButton.isEnabled = false
                    } else {
                        binding.labelNameAttention.visibility = View.GONE
                        binding.registerButton.isEnabled = true
                    }
                }
        compositeDisposable.add(disposable)

        binding.registerButton.setOnClickListener { onClickRegister() }
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
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
