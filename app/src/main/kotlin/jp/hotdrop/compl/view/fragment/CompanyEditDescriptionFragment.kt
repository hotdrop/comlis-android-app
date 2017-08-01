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
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.databinding.FragmentCompanyEditDescriptionBinding
import jp.hotdrop.compl.viewmodel.CompanyEditDescriptionViewModel
import javax.inject.Inject

class CompanyEditDescriptionFragment: BaseFragment() {

    @Inject
    lateinit var viewModel: CompanyEditDescriptionViewModel
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private lateinit var binding: FragmentCompanyEditDescriptionBinding
    private val companyId by lazy { arguments.getInt(EXTRA_COMPANY_ID) }

    companion object {
        fun create(companyId: Int) = CompanyEditDescriptionFragment().apply {
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
        binding = FragmentCompanyEditDescriptionBinding.inflate(inflater, container, false)
        setHasOptionsMenu(false)
        binding.viewModel = viewModel

        viewModel.loadData(companyId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onComplete = { binding.updateButton.setOnClickListener{ onClickUpdate() } },
                        onError =  { showErrorAsToast(ErrorType.LoadFailureCompany, it) }
                )
                .addTo(compositeDisposable)

        return binding.root
    }

    private fun onClickUpdate() {
        viewModel.update()

        val intent = Intent().apply { putExtra(REFRESH_MODE, UPDATE) }
        activity.setResult(Activity.RESULT_OK, intent)

        exit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.dispose()
    }
}