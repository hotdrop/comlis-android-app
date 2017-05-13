package jp.hotdrop.compl.dao

import io.reactivex.Maybe
import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.model.JobEvaluation_Relation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobEvaluationDao @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma

    fun find(companyId: Int): Maybe<JobEvaluation> {
        return relation().selector()
                .companyIdEq(companyId)
                .executeAsObservable()
                .firstElement()
    }

    fun upsert(obj: JobEvaluation) {
        relation().upsert(obj)
    }

    fun countChecked(companyId: Int): Int {
        val je = relation().selector().companyIdEq(companyId).value()
        var cnt = 0
        if(je.correctSentence) cnt++
        if(je.developmentEnv) cnt++
        if(je.wantSkill) cnt++
        if(je.personImage) cnt++
        if(je.appeal) cnt++
        if(je.jobOfferReason) cnt++
        return cnt
    }

    private fun relation(): JobEvaluation_Relation {
        return orma.relationOfJobEvaluation()
    }

}