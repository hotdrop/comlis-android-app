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

    fun insert(obj: JobEvaluation) {
        orma.transactionSync {
            relation().inserter().execute(obj)
        }
    }

    fun update(obj: JobEvaluation) {
        orma.transactionSync {
            relation().updater()
                    .correctSentence(obj.correctSentence)
                    .developmentEnv(obj.developmentEnv)
                    .wantSkill(obj.wantSkill)
                    .personImage(obj.personImage)
                    .appeal(obj.appeal)
                    .jobOfferReason(obj.jobOfferReason)
                    .companyIdEq(obj.companyId)
                    .execute()
        }
    }

    fun count(companyId: Int): Int {
        return relation().selector().companyIdEq(companyId).count()
    }

    private fun relation(): JobEvaluation_Relation {
        return orma.relationOfJobEvaluation()
    }

}