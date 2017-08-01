package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil
import javax.inject.Inject

class CompanyEditOverviewViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    lateinit var viewName: String
    lateinit var viewOverview: String
    lateinit var colorName: String

    private var companyId: Int = -1
    var categoryId: Int = -1

    var isChangeCategory: Boolean = false

    fun loadData(companyId: Int): Completable =
        companyDao.find(companyId)
                .toSingle()
                .flatMapCompletable { company ->
                    setData(company)
                    Completable.complete()
                }

    private fun setData(company: Company) {
        viewName = company.name
        viewOverview = company.overview ?: ""
        colorName = categoryDao.find(company.categoryId).colorType
        companyId = company.id
        categoryId = company.categoryId
    }

    @ColorRes
    fun getColorRes() =
            ColorUtil.getResDark(colorName, context)

    fun existName(name: String) =
        if(name.isBlank())
            false
        else
            companyDao.existExclusionId(name, companyId)

    fun update(selectedCategorySpinnerId: Int) {
        isChangeCategory = (categoryId != selectedCategorySpinnerId)

        val company =  makeCompany(selectedCategorySpinnerId)
        companyDao.updateOverview(company)
    }

    private fun makeCompany(selectedCategorySpinnerId: Int) = Company().apply {
        id = companyId
        name = viewName
        categoryId = selectedCategorySpinnerId
        overview = viewOverview.toStringOrNull()
    }
}