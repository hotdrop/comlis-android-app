package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.repository.company.CompanyRepository
import jp.hotdrop.compl.repository.tag.TagRepository
import javax.inject.Inject

class TagsAssociateViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var tagRepository: TagRepository
    @Inject
    lateinit var companyRepository: CompanyRepository
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    val viewModels: ObservableList<TagAssociateViewModel> = ObservableArrayList()

    private lateinit var callback: Callback
    private val NOT_INIT_COMPANY_ID = -1
    private var companyId = NOT_INIT_COMPANY_ID

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun loadData(companyId: Int) {
        tagRepository.findAll()
                .map { tags ->
                    tags.map {
                        val isAssociatedWith = companyRepository.hasAssociateTag(companyId, it.id)
                        TagAssociateViewModel(it, isAssociatedWith, context)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onSuccess = { initView(it, companyId) },
                        onError = { callback.showError(it) }
                )
                .addTo(compositeDisposable)
    }

    private fun initView(tagAssociateViewModels: List<TagAssociateViewModel>, companyId: Int) {
        this.companyId = companyId
        viewModels.clear()
        viewModels.addAll(tagAssociateViewModels)
    }

    fun update() {
        if(companyId == NOT_INIT_COMPANY_ID) {
            return
        }
        val tags = viewModels
                .filter{ it.isAssociated }
                .map{ it.tag }
                .toList()
        companyRepository.associateTagByCompany(companyId, tags)
    }

    fun destroy() {
        compositeDisposable.clear()
    }

    interface Callback {
        fun showError(throwable: Throwable)
    }
}