package jp.hotdrop.compl.viewmodel

import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.repository.category.CategoryRepository
import javax.inject.Inject

class CompanyRootViewModel @Inject constructor(
        private val categoryRepository: CategoryRepository
): ViewModel() {

    @get:Bindable
    var tabEmptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.tabEmptyMessageVisibility)
        }

    @get:Bindable
    var progressVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.progressVisibility)
        }

    fun loadData(): Single<List<Category>> =
            categoryRepository.findAll().toSingle()

    fun visibilityEmptyMessageOnScreen() {
        tabEmptyMessageVisibility = View.VISIBLE
    }

    fun goneEmptyMessageOnScreen() {
        tabEmptyMessageVisibility = View.GONE
    }

    fun visibilityProgressBar() {
        progressVisibility = View.VISIBLE
    }

    fun goneProgressBar() {
        progressVisibility = View.GONE
    }
}