package jp.hotdrop.comlis.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.comlis.BR
import jp.hotdrop.comlis.R
import jp.hotdrop.comlis.model.Category
import jp.hotdrop.comlis.model.Company
import jp.hotdrop.comlis.model.ReceiveCompany
import jp.hotdrop.comlis.repository.category.CategoryRepository
import jp.hotdrop.comlis.repository.company.CompanyRepository
import jp.hotdrop.comlis.util.ColorUtil
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

    var hasCompaniesFromRemote = false

    fun loadData(): Single<List<Category>> =
            categoryRepository.findAll().toSingle()

    fun loadDataFromRemote(): Completable {
        return companyRepository.findAllFromRemote()
                .flatMapCompletable { receiveCompanies ->
                    hasCompaniesFromRemote = receiveCompanies.isNotEmpty()
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

        if(unNecessaryUpdate(company)) {
            return
        }

        val compositeCompany = company.apply {
            overview = if(overview.isNullOrEmpty()) receiveCompany.overview else overview
            workPlace = if(workPlace.isNullOrEmpty()) receiveCompany.workPlace else workPlace
            employeesNum = if(employeesNum == 0) receiveCompany.employeesNum else employeesNum
            salaryLow = if(salaryLow == 0) receiveCompany.salaryLow else salaryLow
            salaryHigh = if(salaryHigh == 0) receiveCompany.salaryHigh else salaryHigh
        }
        companyRepository.update(compositeCompany, fromRemoteRepository = true)
    }

    private fun unNecessaryUpdate(company: Company) =
            (company.overview != null && company.overview!!.isNotEmpty()) &&
                (company.workPlace != null && company.workPlace!!.isNotEmpty()) &&
                company.employeesNum > 0 && company.salaryLow > 0 && company.salaryHigh > 0

    fun getErrorMessage(throwable: Throwable): String {
        val httpException = throwable as? HttpException
        if(httpException != null) {
            val error = httpException.response().errorBody()
            return error.toString() + " http status code=" + httpException.code()
        }
        return throwable.message ?: "unknown error. throwable message is null."
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