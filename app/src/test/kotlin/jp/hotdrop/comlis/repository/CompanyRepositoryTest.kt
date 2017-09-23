package jp.hotdrop.comlis.repository

import android.content.Context
import android.support.test.InstrumentationRegistry
import jp.hotdrop.comlis.model.Company
import jp.hotdrop.comlis.model.JobEvaluation
import jp.hotdrop.comlis.model.OrmaDatabase
import jp.hotdrop.comlis.model.Tag
import jp.hotdrop.comlis.repository.company.CompanyLocalDataSource
import jp.hotdrop.comlis.repository.company.CompanyRemoteDataSource
import jp.hotdrop.comlis.repository.company.CompanyRepository
import jp.hotdrop.comlis.repository.company.JobEvaluationLocalDataSource
import jp.hotdrop.comlis.repository.tag.TagLocalDataSource
import jp.hotdrop.comlis.repository.tag.TagRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(23), manifest = Config.NONE)
class CompanyRepositoryTest {

    private lateinit var companyRepository: CompanyRepository
    private lateinit var tagRepository: TagRepository

    private fun getContext(): Context =
            InstrumentationRegistry.getTargetContext()

    @Before
    fun setup() {
        val orma = OrmaDatabase.builder(getContext()).name(null).build()
        val ormaHolder = OrmaHolder(orma)

        val tagLocalDataSource = TagLocalDataSource(ormaHolder)
        tagRepository = TagRepository(tagLocalDataSource)

        val companyLocalDataSource = CompanyLocalDataSource(ormaHolder, tagRepository)
        val companyRemoteDataSource = CompanyRemoteDataSource(MockComlisClient().service())

        val jobEvaluateDataSource = JobEvaluationLocalDataSource(ormaHolder)
        companyRepository = CompanyRepository(companyLocalDataSource, companyRemoteDataSource, jobEvaluateDataSource)
    }

    @Test
    fun findTest() {
        val company = createCompany("TestFind")
        companyRepository.insert(company)

        val companyGetDb = companyRepository.findAll().blockingGet()[0]
        val companyGetDbById = companyRepository.find(companyGetDb.id)
        assertCompareCompany(company, companyGetDb)
        assertCompareCompany(companyGetDb, companyGetDbById)
        assertCompareCompany(companyGetDbById, company)
    }

    @Test
    fun findByTagTest() {
        val c1 = createCompany("Tag1And2")
        val c2 = createCompany("Tag2And3")
        companyRepository.insert(c1)
        companyRepository.insert(c2)
        mutableListOf(createTag("Tag1"), createTag("Tag2"), createTag("Tag3"), createTag("Tag4"))
                .forEach { tagRepository.insert(it) }

        val companiesFromDB = companyRepository.findAll().blockingGet().toList()
        val tagsFromDB = tagRepository.findAll().blockingGet().toList()

        val company1FromDB = companiesFromDB[0]
        val company1AttachTag = mutableListOf(tagsFromDB[0], tagsFromDB[1])
        companyRepository.associateTagByCompany(company1FromDB.id, company1AttachTag)

        val company2FromDB = companiesFromDB[1]
        val company2AttachTag = mutableListOf(tagsFromDB[1], tagsFromDB[2], tagsFromDB[3])
        companyRepository.associateTagByCompany(company2FromDB.id, company2AttachTag)

        val company1Tags = companyRepository.findByTag(company1FromDB.id)
        company1Tags.zip(company1AttachTag).forEach { assertCompareTag(it.first, it.second) }
    }

    @Test
    fun insertTest() {
        val testName = "Test900"
        val company = createCompany(testName)
        companyRepository.insert(company)

        val companyFromDB = companyRepository.findAll()
                .blockingGet()
                .toList()
                .first { c -> c.name == testName }

        assertCompareCompany(company, companyFromDB)
        println("viewOrder=${companyFromDB.viewOrder}")

        assert(companyFromDB.registerDate != null)
        println("register date = ${companyFromDB.registerDate}")

        assert(companyFromDB.updateDate == null)
        companyRepository.delete(companyFromDB.id)
    }

    @Test
    fun updateTest() {
        val testName = "Update Test"
        val company = createCompany(testName)
        companyRepository.insert(company)

        val companyChangeData = companyRepository.findAll()
                .blockingGet()
                .toList()
                .first { c -> c.name == testName }
                .apply {
                    categoryId = 2
                    overview = "update overview"
                    employeesNum = 499
                    salaryLow = 299
                    salaryHigh = 399
                    wantedJob = " update wanted job"
                    url = "https://www.update.up.date"
                    note = "update description"
                }
        companyRepository.update(companyChangeData)

        val companyFromDB = companyRepository.find(companyChangeData.id)

        assertCompareCompany(companyChangeData, companyFromDB)
        assert(companyChangeData.viewOrder == companyFromDB.viewOrder)
        assert(companyChangeData.registerDate == companyFromDB.registerDate)
        assert(companyFromDB.updateDate != null)
        println("update date = ${companyFromDB.updateDate}")

        companyRepository.delete(companyFromDB.id)
    }

    @Test
    fun upsertJovEvaluationForInsertTest() {
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
        companyRepository.upsertJobEvaluation(je)
        val je2 = companyRepository.findJobEvaluation(testCompanyId)

        assert(equalsJobEvaluation(je, je2!!))
    }

    @Test
    fun upsertJovEvaluationForUpdateTest() {
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
        companyRepository.upsertJobEvaluation(je)
        je.jobOfferReason = true
        companyRepository.upsertJobEvaluation(je)
        val je2 = companyRepository.findJobEvaluation(testCompanyId)
        assert(equalsJobEvaluation(je, je2!!))
    }

    private fun createCompany(argName: String) = Company().apply {
            name = argName
            categoryId = 1
            overview = "this is a overview! 概要です。"
            employeesNum = 500
            salaryLow = 200
            salaryHigh = 300
            wantedJob = "wanted jobs! 募集職種です。"
            url = "https://www.google.co.jp"
            note = "description! 詳細です。"
            favorite = 3
    }

    private fun createTag(argName: String) = Tag().apply {
        name = argName
        colorType = "ブルー"
    }

    private fun assertCompareCompany(c1: Company, c2: Company) {
        assert(c1.categoryId == c2.categoryId)
        assert(c1.overview == c2.overview)
        assert(c1.employeesNum == c2.employeesNum)
        assert(c1.salaryLow == c2.salaryLow)
        assert(c1.salaryHigh == c2.salaryHigh)
        assert(c1.wantedJob == c2.wantedJob)
        assert(c1.url == c2.url)
        assert(c1.note == c2.note)
        assert(c1.favorite == c2.favorite)
    }

    private fun assertCompareTag(t1: Tag, t2: Tag) {
        assert(t1.name == t2.name)
        assert(t1.colorType == t2.colorType)
        assert(t1.registerDate == t1.registerDate)
    }

    private fun equalsJobEvaluation(je1: JobEvaluation, je2: JobEvaluation): Boolean {
        return (je1.companyId == je2.companyId &&
                je1.correctSentence == je2.correctSentence &&
                je1.developmentEnv == je2.developmentEnv &&
                je1.wantSkill == je2.wantSkill &&
                je1.personImage == je2.personImage &&
                je1.appeal == je2.appeal &&
                je1.jobOfferReason == je2.jobOfferReason)
    }
}