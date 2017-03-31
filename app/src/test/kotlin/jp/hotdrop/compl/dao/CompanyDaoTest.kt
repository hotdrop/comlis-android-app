package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.model.Company
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyDaoTest {

    private fun getContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    @Before
    fun setup() {
        OrmaHolder.initialize(getContext())
    }

    @Test
    fun insertTest() {
        val testName = "Test900"
        val c1 = createCompany(testName)
        CompanyDao.insert(c1)

        CompanyDao.findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe (
                    { list -> list.filter { c2 -> c2.name == testName }.forEach { c2 ->
                        assert(compareCompany(c1, c2))
                        println("viewOrder=${c2.viewOrder}")
                        assert(c2.registerDate != null)
                        println("register date = ${c2.registerDate}")
                        assert(c2.updateDate == null)
                        CompanyDao.delete(c2)
                    }},
                    { throwable -> println("Error! Super Error! No！ No！ Noooooo!" + throwable.message) }
        )
    }

    @Test
    fun updateTest() {
        val testName = "Update Test"
        val c1 = createCompany(testName)
        CompanyDao.insert(c1)

        CompanyDao.findAll()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .subscribe (
                    { list -> list.filter { c2 -> c2.name == testName }.forEach { c2 ->
                        c2.categoryId = 2
                        c2.overview = "update overview"
                        c2.employeesNum = 499
                        c2.salaryLow = 299
                        c2.salaryHigh = 399
                        c2.wantedJob = " update wanted job"
                        c2.url = "https://www.update.up.date"
                        c2.note = "update description"
                        CompanyDao.update(c2)
                        CompanyDao.findAll()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe (
                                    { list -> list.filter { c3 -> c3.name == testName }.forEach { c3 ->
                                        assert(compareCompany(c2, c3))
                                        assert(c2.viewOrder == c3.viewOrder)
                                        assert(c2.registerDate == c3.registerDate)
                                        assert(c3.updateDate != null)
                                        println("update date = ${c3.updateDate}")
                                        CompanyDao.delete(c3)
                                    }},
                                    { throwable -> println("More More Error! Noooooo!" + throwable.message) }
                                )
                    } },
                    { throwable -> println("Error! Super Error! No！ No！ Noooooo!" + throwable.message) }
        )


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