package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject

class CompaniesViewModel @Inject constructor(private val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var jobEvaluationDao: JobEvaluationDao
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private var viewModels: ObservableList<CompanyViewModel> = ObservableArrayList()
    private lateinit var callback: Callback
    private var movedItem = false

    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun loadData(categoryId: Int) {
        val disposable = companyDao.findByCategory(categoryId)
                .map { companies ->
                    companies.map {
                        // TODO Dao渡しまくるのなんとかしたい・・
                        CompanyViewModel(it, context, companyDao, categoryDao, jobEvaluationDao)
                    }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onSuccess(it) },
                        { callback.showError(it) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onSuccess(companyViewModels: List<CompanyViewModel>) {
        viewModels.clear()
        viewModels.addAll(companyViewModels)
        checkAndUpdateEmptyMessageVisibility()
    }

    fun stop() {
        compositeDisposable.dispose()
    }

    fun destroy() {
        compositeDisposable.clear()
    }

    fun getViewModels(): ObservableList<CompanyViewModel> {
        return viewModels
    }

    fun updateItemOrder() {
        if(movedItem) {
            val companyIds = viewModels.map { it.getId() }
            companyDao.updateAllOrder(companyIds)
            movedItem = false
        }
    }

    fun update(companyId: Int) {
        val idx = viewModels.indexOf(viewModels.find { it.getId() == companyId })
        val newCompany = companyDao.find(companyId)
        viewModels[idx] = CompanyViewModel(newCompany, context, companyDao, categoryDao, jobEvaluationDao)
    }

    fun delete(companyId: Int) {
        viewModels.remove(viewModels.find { it.getId() == companyId })
        checkAndUpdateEmptyMessageVisibility()
    }

    fun movedItem() {
        movedItem = true
    }

    fun getTagAssociateViewModel(tag: Tag): TagAssociateViewModel {
        // 関連付けしているタグしか取得していないため、無条件で第二引数をtrueにする。（関連付けられているという意味）
        return TagAssociateViewModel(tag, true, context)
    }

    private fun checkAndUpdateEmptyMessageVisibility() {
        if(viewModels.isNotEmpty()) {
            emptyMessageVisibility = View.GONE
        } else {
            emptyMessageVisibility = View.VISIBLE
        }
    }

    interface Callback {
        fun showError(throwable: Throwable)
    }
}