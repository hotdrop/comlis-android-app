package jp.hotdrop.comlis.repository.company

import jp.hotdrop.comlis.model.JobEvaluation
import jp.hotdrop.comlis.repository.OrmaHolder
import javax.inject.Inject

class JobEvaluationLocalDataSource @Inject constructor(
        ormaHolder: OrmaHolder
) {

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