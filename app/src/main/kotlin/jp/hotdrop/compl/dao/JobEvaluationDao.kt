package jp.hotdrop.compl.dao

import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.model.JobEvaluation_Relation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobEvaluationDao @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma

    fun find(companyId: Int): JobEvaluation? {
        return relation().selector().companyIdEq(companyId).valueOrNull()
    }

    fun upsert(obj: JobEvaluation) {
        relation().upsert(obj)
    }

    private fun relation(): JobEvaluation_Relation {
        return orma.relationOfJobEvaluation()
    }

}