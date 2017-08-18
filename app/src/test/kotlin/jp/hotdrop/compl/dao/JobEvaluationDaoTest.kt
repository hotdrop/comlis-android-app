package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.model.OrmaDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JobEvaluationDaoTest {

    private lateinit var dao: JobEvaluationDao

    private fun getContext(): Context =
            InstrumentationRegistry.getTargetContext()

    @Before
    fun setup() {
        val orma = OrmaDatabase.builder(getContext()).name(null).build()
        val ormaHolder = OrmaHolder(orma)
        dao = JobEvaluationDao(ormaHolder)
    }

    @Test
    fun upsertForInsertTest() {
        val testCompanyId = 50
        val je = JobEvaluation().apply {
            companyId = testCompanyId
            correctSentence = true
            developmentEnv = false
            wantSkill = true
            personImage = false
            appeal = true
            jobOfferReason = false
        }
        dao.upsert(je)
        val je2 = dao.find(testCompanyId)

        assert(equal(je, je2!!))
    }

    @Test
    fun upsertForUpdateTest() {
        val testCompanyId = 60
        val je = JobEvaluation().apply {
            companyId = testCompanyId
            correctSentence = true
            developmentEnv = false
            wantSkill = false
            personImage = false
            appeal = false
            jobOfferReason = false
        }
        dao.upsert(je)
        je.jobOfferReason = true
        dao.upsert(je)
        val je2 = dao.find(testCompanyId)
        assert(equal(je, je2!!))
    }

    private fun equal(je1: JobEvaluation, je2: JobEvaluation): Boolean {
        return (je1.companyId == je2.companyId &&
                je1.correctSentence == je2.correctSentence &&
                je1.developmentEnv == je2.developmentEnv &&
                je1.wantSkill == je2.wantSkill &&
                je1.personImage == je2.personImage &&
                je1.appeal == je2.appeal &&
                je1.jobOfferReason == je2.jobOfferReason)
    }
}