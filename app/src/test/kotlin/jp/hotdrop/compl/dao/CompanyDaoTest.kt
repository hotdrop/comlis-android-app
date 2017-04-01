package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.OrmaDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyDaoTest {

    private lateinit var orma: OrmaDatabase

    private fun getContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    @Before
    fun setup() {
        OrmaHolder.initialize(getContext(), false)
        orma = OrmaHolder.buildDB
    }

    @Test
    fun insertTest() {
        val testName = "Test900"
        val company = createCompany(testName)
        CompanyDao.insert(company)

        val companyFromDB = CompanyDao.findAll().blockingGet().toList()
                .filter { c -> c.name == testName }
                .first()

        assert(compareCompany(company, companyFromDB))
        println("viewOrder=${companyFromDB.viewOrder}")
        assert(companyFromDB.registerDate != null)
        println("register date = ${companyFromDB.registerDate}")
        assert(companyFromDB.updateDate == null)
        CompanyDao.delete(companyFromDB)
    }

    @Test
    fun updateTest() {
        val testName = "Update Test"
        val company = createCompany(testName)
        CompanyDao.insert(company)

        val companyChangeData = CompanyDao.findAll().blockingGet().toList()
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
        CompanyDao.update(companyChangeData)

        val companyFromDB = CompanyDao.find(companyChangeData.id)

        assert(compareCompany(companyChangeData, companyFromDB))
        assert(companyChangeData.viewOrder == companyFromDB.viewOrder)
        assert(companyChangeData.registerDate == companyFromDB.registerDate)
        assert(companyFromDB.updateDate != null)
        println("update date = ${companyFromDB.updateDate}")

        CompanyDao.delete(companyFromDB)
    }

    private fun createCompany(argName: String): Company {
        return Company().apply {
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
    }

    private fun compareCompany(c1: Company, c2: Company): Boolean {
        assert(c1.categoryId == c2.categoryId)
        assert(c1.overview == c2.overview)
        assert(c1.employeesNum == c2.employeesNum)
        assert(c1.salaryLow == c2.salaryLow)
        assert(c1.salaryHigh == c2.salaryHigh)
        assert(c1.wantedJob == c2.wantedJob)
        assert(c1.url == c2.url)
        assert(c1.note == c2.note)
        assert(c1.favorite == c2.favorite)
        return true
    }
}