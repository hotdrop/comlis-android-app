package jp.hotdrop.compl.view.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.databinding.FragmentCompanyEditInfoBinding
import jp.hotdrop.compl.viewmodel.CompanyEditInfoViewModel
import javax.inject.Inject

class CompanyEditInfoFragment: BaseFragment() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var viewModel: CompanyEditInfoViewModel

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
        val disposable = viewModel.loadData(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { binding.viewModel = viewModel },
                        { e -> Toast.makeText(activity, "failed load companies." + e.message, Toast.LENGTH_LONG).show()}
                )
        compositeDisposable.add(disposable)

        binding.updateButton.setOnClickListener{ onClickUpdate() }
        return binding.root
    }

    private fun onClickUpdate() {
        val errorMessage = viewModel.update()
        if(errorMessage == null) {
            onSuccess()
        } else {
            Toast.makeText(context, errorMessage.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun onSuccess() {
        val intent = Intent().apply { putExtra(REFRESH_MODE, UPDATE) }
        activity.setResult(Activity.RESULT_OK, intent)
        exit()
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }
}