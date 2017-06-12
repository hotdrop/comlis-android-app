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
                    categories.map {
                        val itemCount = getRegisterCompanyCount(it.id)
                        CategoryViewModel(it, itemCount, context)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onSuccess(it) },
                        { callback.showError(it) }
                )
        compositeDisposable.add(disposable)
    }

    fun destroy() {
        compositeDisposable.clear()
    }

    private fun onSuccess(categoryViewModels: List<CategoryViewModel>) {
        viewModels.clear()
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
        val itemCount = getRegisterCompanyCount(category.id)
        viewModels.add(CategoryViewModel(category, itemCount, context))
        checkAndUpdateEmptyMessageVisibility()
    }

    fun update(vm: CategoryViewModel, newName: String, newColorType: String) {
        val c = vm.category.apply {
            name = newName
            colorType = newColorType
        }
        categoryDao.update(c)
        val idx = viewModels.indexOf(vm)
        viewModels[idx] = CategoryViewModel(c, vm.registerCompanyCount, context)
    }

    fun updateItemOrder() {
        val categoryIds = viewModels.map { it.getId() }
        categoryDao.updateAllOrder(categoryIds)
    }

    fun delete(vm: CategoryViewModel) {
        categoryDao.delete(vm.category)
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

    private fun getRegisterCompanyCount(categoryId: Int): Int {
        return companyDao.countByCategory(categoryId)
    }

    interface Callback {
        fun showError(throwable: Throwable)
    }
}