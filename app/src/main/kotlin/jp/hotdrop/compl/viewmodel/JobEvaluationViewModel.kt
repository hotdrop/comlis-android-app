package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import io.reactivex.rxkotlin.toSingle
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.repository.category.CategoryRepository
import jp.hotdrop.compl.repository.company.CompanyRepository
import jp.hotdrop.compl.util.ColorUtil
import javax.inject.Inject

class JobEvaluationViewModel @Inject constructor(val context: Context) {

    @Inject
    lateinit var companyRepository: CompanyRepository
    @Inject
    lateinit var categoryRepository: CategoryRepository

    private var companyId = -1
    private lateinit var colorName: String

    var viewCorrectSentence = false
    var viewDevelopmentEnv= false
    var viewWantSkill= false
    var viewPersonImage= false
    var viewAppeal= false
    var viewJobOfferReason= false

    fun loadData(companyId: Int): Completable =
            companyRepository.find(companyId)
                    .toSingle()
                    .flatMapCompletable { company ->
                        setData(company)
                        Completable.complete()
                    }

    private fun setData(company: Company) {
        companyId = company.id
        colorName = categoryRepository.find(company.categoryId).colorType

        companyRepository.findJobEvaluation(company.id)?.run {
            viewCorrectSentence = correctSentence
            viewDevelopmentEnv= developmentEnv
            viewWantSkill= wantSkill
            viewPersonImage= personImage
            viewAppeal= appeal
            viewJobOfferReason= jobOfferReason
        }
    }

    @ColorRes
    fun getColorRes() =
            ColorUtil.getResDark(colorName, context)

    fun update() {
        companyRepository.upsertJobEvaluation(makeData())
    }

    private fun makeData() = JobEvaluation().apply {
        companyId = this@JobEvaluationViewModel.companyId
        correctSentence = viewCorrectSentence
        developmentEnv = viewDevelopmentEnv
        wantSkill = viewWantSkill
        personImage = viewPersonImage
        appeal = viewAppeal
        jobOfferReason = viewJobOfferReason
    }
}