package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.util.ColorUtil
import javax.inject.Inject

class CompanyEditDescriptionViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    lateinit var viewNote: String
    lateinit var colorName: String

    private var companyId: Int = -1

    fun loadData(companyId: Int): Completable {
        return companyDao.findObserve(companyId)
                .flatMapCompletable { company ->
                    setData(company)
                    Completable.complete()
                }
    }

    private fun setData(company: Company) {
        viewNote = company.note ?: ""
        colorName = categoryDao.find(company.categoryId).colorType
        companyId = company.id
    }

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResDark(colorName, context)

    fun update() {
        val company =  makeCompany()
        companyDao.updateDescription(company)
    }

    private fun makeCompany() = Company().apply {
        id = companyId
        note = viewNote.toStringOrNull()
    }
}