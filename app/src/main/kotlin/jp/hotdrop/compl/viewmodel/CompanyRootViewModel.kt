package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.R
import jp.hotdrop.compl.model.Category
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.ReceiveCompany
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
import jp.hotdrop.compl.util.ColorUtil
import retrofit2.HttpException
import javax.inject.Inject

class CompanyRootViewModel @Inject constructor(
        private val context: Context,
        private val categoryRepository: CategoryRepository,
        private val companyRepository: CompanyRepository
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

    fun loadDataFromRemote(): Completable {
        return companyRepository.findAllFromRemote()
                .flatMapCompletable { receiveCompanies ->
                    receiveCompanies.forEach {
                        if(companyRepository.exist(it.name)) {
                            val company = companyRepository.find(it.name)
                            updateAlreadyCompany(company, it)
                        } else {
                            registerNewCompany(it)
                        }
                    }
                    // TODO 次回以降、取得不要なデータのIDをListにして投げる
                    Completable.complete()
                }
    }

    private fun registerNewCompany(receiveCompany: ReceiveCompany) {

        val categoryName = context.getString(R.string.category_name_get_from_remote)
        if(!categoryRepository.exist(categoryName)) {
            categoryRepository.insert(Category().apply {
                name = categoryName
                colorType = ColorUtil.BLUE_NAME
            })
        }

        val category = categoryRepository.find(categoryName)
        val company = receiveCompany.toCompany()
        company.categoryId = category.id
        companyRepository.insert(company, fromRemoteRepository = true)
    }

    private fun updateAlreadyCompany(company: Company, receiveCompany: ReceiveCompany) {
        val compositeCompany = company.apply {
            overview = if(overview.isNullOrEmpty()) receiveCompany.overview else overview
            workPlace = if(workPlace.isNullOrEmpty()) receiveCompany.workPlace else workPlace
            employeesNum = if(employeesNum == 0) receiveCompany.employeesNum else employeesNum
            salaryLow = if(salaryLow == 0) receiveCompany.salaryLow else salaryLow
            salaryHigh = if(salaryHigh == 0) receiveCompany.salaryHigh else salaryHigh
        }
        companyRepository.update(compositeCompany, fromRemoteRepository = true)
    }

    fun getErrorMessage(throwable: Throwable): String {
        val httpException = throwable as? HttpException
        if(httpException != null) {
            // TODO ステータスコードによってエラーメッセージを分ける
            return "status code=" + httpException.code()
        }
        return throwable.message ?: "不明のエラーです。"
    }

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