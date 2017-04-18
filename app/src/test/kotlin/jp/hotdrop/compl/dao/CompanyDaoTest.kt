package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.OrmaDatabase
import jp.hotdrop.compl.model.Tag
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyDaoTest {

    private lateinit var companyDao: CompanyDao
    private lateinit var tagDao: TagDao

    private fun getContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    @Before
    fun setup() {
        val orma = OrmaDatabase.builder(getContext()).name(null).build()
        val ormaHolder = OrmaHolder(orma)
        tagDao = TagDao(ormaHolder)
        companyDao = CompanyDao(ormaHolder)
    }

    @Test
    fun findTest() {
        val company = createCompany("TestFind")
        companyDao.insert(company)

        val companyGetDb = companyDao.findAll().blockingGet()[0]
        val companyGetDbById = companyDao.find(companyGetDb.id)
        assertCompareCompany(company, companyGetDb)
        assertCompareCompany(companyGetDb, companyGetDbById)
        assertCompareCompany(companyGetDbById, company)
    }

    @Test
    fun findByTagTest() {
        val c1 = createCompany("Tag1And2")
        val c2 = createCompany("Tag2And3")
        companyDao.insert(c1)
        companyDao.insert(c2)
        mutableListOf(createTag("Tag1"), createTag("Tag2"), createTag("Tag3"), createTag("Tag4"))
                .forEach { tagDao.insert(it) }

        val companiesFromDB = companyDao.findAll().blockingGet().toList()
        val tagsFromDB = tagDao.findAll().blockingGet().toList()

        val company1FromDB = companiesFromDB[0]
        val company1AttachTag = mutableListOf(tagsFromDB[0], tagsFromDB[1])
        val company2FromDB = companiesFromDB[1]
        val company2AttachTag = mutableListOf(tagsFromDB[1], tagsFromDB[2], tagsFromDB[3])
        companyDao.associateTagByCompany(company1FromDB.id, company1AttachTag)
        companyDao.associateTagByCompany(company2FromDB.id, company2AttachTag)

        val company1Tags = companyDao.findByTag(company1FromDB.id)
        company1Tags.zip(company1AttachTag).forEach { assertCompareTag(it.first, it.second) }
    }

    @Test
    fun insertTest() {
        val testName = "Test900"
        val company = createCompany(testName)
        companyDao.insert(company)

        val companyFromDB = companyDao.findAll().blockingGet().toList()
                .filter { c -> c.name == testName }
                .first()

        assertCompareCompany(company, companyFromDB)
        println("viewOrder=${companyFromDB.viewOrder}")
        assert(companyFromDB.registerDate != null)
        println("register date = ${companyFromDB.registerDate}")
        assert(companyFromDB.updateDate == null)
        companyDao.delete(companyFromDB)
    }

    @Test
    fun updateTest() {
        val testName = "Update Test"
        val company = createCompany(testName)
        companyDao.insert(company)

        val companyChangeData = companyDao.findAll().blockingGet().toList()
                .filter { c -> c.name == testName }
                .first().apply {
                    categoryId = 2
                    overview = "update overview"
                    employeesNum = 499
                    salaryLow = 299
                    salaryHigh = 399
                    wantedJob = " update wanted job"
                    url = "https://www.update.up.date"
                    note = "update description"
                }
        companyDao.update(companyChangeData)

        val companyFromDB = companyDao.find(companyChangeData.id)

        assertCompareCompany(companyChangeData, companyFromDB)
        assert(companyChangeData.viewOrder == companyFromDB.viewOrder)
        assert(companyChangeData.registerDate == companyFromDB.registerDate)
        assert(companyFromDB.updateDate != null)
        println("update date = ${companyFromDB.updateDate}")

        companyDao.delete(companyFromDB)
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
}