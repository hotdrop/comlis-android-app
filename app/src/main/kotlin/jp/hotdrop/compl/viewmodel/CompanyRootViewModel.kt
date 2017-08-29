package jp.hotdrop.compl.viewmodel

import android.databinding.Bindable
import android.view.View
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.repository.category.CategoryRepository
import java.util.concurrent.TimeUnit
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

    private var dummyChanged: Boolean = true
    fun dummyLoadDataForRemote(): Completable {
        return dummyRemoteList().delay(5000.toLong(), TimeUnit.MILLISECONDS)
                .flatMapCompletable {
                    if(!dummyChanged) {
                        dummyChanged = true
                        throw Exception("Dummy Error")
                    }
                    dummyChanged = false
                    Completable.complete()
                }
    }

    private fun dummyRemoteList(): Single<List<String>> = arrayListOf("1", "2", "3").toSingle()

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