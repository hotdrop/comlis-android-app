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
        orma.transactionSync {
            relation().upsert(obj)
        }
    }

    fun count(companyId: Int): Int {
        return relation().selector().companyIdEq(companyId).count()
    }

    private fun relation(): JobEvaluation_Relation {
        return orma.relationOfJobEvaluation()
    }

}