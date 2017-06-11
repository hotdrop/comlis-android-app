package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import javax.inject.Inject

class CategoriesViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var categoryDao: CategoryDao
    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    private var viewModels: ObservableList<CategoryViewModel> = ObservableArrayList()
    private lateinit var callback: Callback

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun loadData() {
        val disposable = categoryDao.findAll()
                .map { categories ->
                    categories.map { CategoryViewModel(it, context, categoryDao, companyDao) }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { categoryViewModels -> onSuccess(categoryViewModels) },
                        { throwable -> callback.showError(throwable)}
                )
        compositeDisposable.add(disposable)
    }

    fun destroy() {
        compositeDisposable.clear()
    }

    private fun onSuccess(categoryViewModels: List<CategoryViewModel>) {
        if(categoryViewModels.isNotEmpty()) {
            viewModels.addAll(categoryViewModels)
        }
        checkAndUpdateEmptyMessageVisibility()
    }

    fun getViewModels(): ObservableList<CategoryViewModel> {
        return viewModels    }

    fun existName(categoryName: String): Boolean {
        return categoryDao.exist(categoryName)
    }

    fun existNameExclusionId(categoryName: String, id: Int): Boolean {
        return categoryDao.existExclusionId(categoryName, id)
    }

    fun register(categoryName: String, colorType: String) {
        categoryDao.insert(categoryName, colorType)
        val category = categoryDao.find(categoryName)
        val vm = CategoryViewModel(category, context, categoryDao, companyDao)
        viewModels.add(vm)
        checkAndUpdateEmptyMessageVisibility()
    }

    fun update(vm: CategoryViewModel, newName: String, newColorType: String) {
        val category = vm.category.apply {
            name = newName
            colorType = newColorType
        }
        categoryDao.update(category)
        val newVm = CategoryViewModel(category, context, categoryDao, companyDao)
        val idx = viewModels.indexOf(vm)
        viewModels[idx].change(newVm)
    }

    fun updateItemOrder() {
        val categoryIds = viewModels.map { it.getId() }
        categoryDao.updateAllOrder(categoryIds)
    }

    fun delete(vm: CategoryViewModel) {
        viewModels.remove(vm)
        checkAndUpdateEmptyMessageVisibility()
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