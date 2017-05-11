package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import io.reactivex.Completable
import jp.hotdrop.compl.dao.CategoryDao
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.dao.JobEvaluationDao
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.util.ColorUtil
import javax.inject.Inject

class JobEvaluationViewModel @Inject constructor(val context: Context) {

    @Inject
    lateinit var jobEvaluationDao: JobEvaluationDao
    @Inject
    lateinit var companyDao: CompanyDao
    @Inject
    lateinit var categoryDao: CategoryDao

    lateinit var colorName: String

    var viewCorrectSentence = false
    var viewDevelopmentEnv= false
    var viewWantSkill= false
    var viewPersonImage= false
    var viewAppeal= false
    var viewJobOfferReason= false

    fun loadData(companyId: Int): Completable {
        return companyDao.findObserve(companyId)
                .flatMapCompletable { company ->
                    setData(company)
                }

    }

    private fun setData(company: Company): Completable {
        colorName = categoryDao.find(company.categoryId).colorType
        return jobEvaluationDao.find(company.id).flatMapCompletable { je ->
            viewCorrectSentence = je.correctSentence
            viewDevelopmentEnv= je.developmentEnv
            viewWantSkill= je.wantSkill
            viewPersonImage= je.personImage
            viewAppeal= je.appeal
            viewJobOfferReason= je.jobOfferReason
            Completable.complete()
        }
    }

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResDark(colorName, context)
    }

    fun update() {
        jobEvaluationDao.upsert(makeData())
    }

    private fun makeData() = JobEvaluation().apply {
        correctSentence = viewCorrectSentence
        developmentEnv = viewDevelopmentEnv
        wantSkill = viewWantSkill
        personImage = viewPersonImage
        appeal = viewAppeal
        jobOfferReason = viewJobOfferReason
    }
}