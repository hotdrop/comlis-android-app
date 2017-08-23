package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
import jp.hotdrop.compl.util.ColorUtil
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