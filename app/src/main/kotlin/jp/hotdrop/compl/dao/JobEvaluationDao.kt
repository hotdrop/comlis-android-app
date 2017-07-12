package jp.hotdrop.compl.dao

import jp.hotdrop.compl.model.JobEvaluation
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JobEvaluationDao @Inject constructor(ormaHolder: OrmaHolder) {

    private val orma = ormaHolder.orma

    fun find(companyId: Int): JobEvaluation? =
            relation().selector()
                    .companyIdEq(companyId)
                    .valueOrNull()

    fun upsert(obj: JobEvaluation) {
        relation().upsert(obj)
    }

    private fun relation() =
            orma.relationOfJobEvaluation()
}