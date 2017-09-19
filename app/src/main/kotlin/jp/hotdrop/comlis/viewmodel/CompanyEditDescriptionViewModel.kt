package jp.hotdrop.comlis.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.comlis.model.Company
import jp.hotdrop.comlis.repository.category.CategoryRepository
import jp.hotdrop.comlis.repository.company.CompanyRepository
import jp.hotdrop.comlis.util.ColorUtil
import javax.inject.Inject

class CompanyEditDescriptionViewModel @Inject constructor(
        private val context: Context,
        private val companyRepository: CompanyRepository,
        private val categoryRepository: CategoryRepository
): ViewModel() {

    lateinit var viewNote: String
    lateinit var colorName: String

    private var companyId: Int = -1

    fun loadData(companyId: Int): Completable =
            companyRepository.find(companyId)
                    .toSingle()
                    .flatMapCompletable { company ->
                        setData(company)
                        Completable.complete()
                    }

    private fun setData(company: Company) {
        viewNote = company.note ?: ""
        colorName = categoryRepository.find(company.categoryId).colorType
        companyId = company.id
    }

    @ColorRes
    fun getColorRes() = ColorUtil.getResDark(colorName, context)

    fun update() {
        val company =  makeCompany()
        companyRepository.updateDescription(company)
    }

    private fun makeCompany() = Company().apply {
        id = companyId
        note = viewNote.toStringOrNull()
    }
}