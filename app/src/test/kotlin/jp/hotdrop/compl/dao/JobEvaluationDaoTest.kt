package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.reactivex.schedulers.TestScheduler
import jp.hotdrop.compl.model.JobEvaluation
import jp.hotdrop.compl.model.OrmaDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JobEvaluationDaoTest {

    private lateinit var dao: JobEvaluationDao

    private fun getContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    @Before
    fun setup() {
        val orma = OrmaDatabase.builder(getContext()).name(null).build()
        val ormaHolder = OrmaHolder(orma)
        dao = JobEvaluationDao(ormaHolder)
    }

    @Test
    fun upsertForInsertTest() {
        val testCompanyId = 1
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
        dao.find(testCompanyId).subscribeOn(TestScheduler())
                .observeOn(TestScheduler())
                .subscribe(
                        { je2 -> assert(equal(je, je2)) },
                        { e -> error("If the value obtained from DB is NULL." + e.message) }
                ).dispose()
    }

    @Test
    fun upsertForUpdateTest() {
        val testCompanyId = 2
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
        dao.find(testCompanyId).subscribeOn(TestScheduler())
                .observeOn(TestScheduler())
                .subscribe(
                        { je2 -> assert(equal(je, je2)) },
                        { e -> error("If the value obtained from DB is NULL." + e.message) }
                ).dispose()
    }

    @Test
    fun countTest() {
        val testId1 = 1
        dao.upsert(JobEvaluation().apply { companyId = testId1 })

        val testId2 = 2
        dao.upsert(JobEvaluation().apply {
            companyId = testId2
            correctSentence = true
        })
        val testId3 = 3
        dao.upsert(JobEvaluation().apply {
            companyId = testId3
            correctSentence = true
            developmentEnv = true
            wantSkill = true
            appeal = true
            personImage = true
            jobOfferReason = true
        })

        dao.find(testId1).subscribeOn(TestScheduler())
                .observeOn(TestScheduler())
                .subscribe(
                        { je ->
                            val cntZero = dao.countChecked(je.companyId)
                            assert(cntZero == 0)
                        },
                        { e -> error("count zero test error." + e.message) }
                ).dispose()
        dao.find(testId2).subscribeOn(TestScheduler())
                .observeOn(TestScheduler())
                .subscribe(
                        { je ->
                            val cntOne = dao.countChecked(je.companyId)
                            assert(cntOne == 1)
                        },
                        { e -> error("count one test error." + e.message) }
                ).dispose()
        dao.find(testId3).subscribeOn(TestScheduler())
                .observeOn(TestScheduler())
                .subscribe(
                        { je ->
                            val cntSix = dao.countChecked(je.companyId)
                            assert(cntSix == 6)
                        },
                        { e -> error("count six test error." + e.message) }
                ).dispose()
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