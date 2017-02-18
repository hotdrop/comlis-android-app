package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
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
    fun insertData() {
        var comp1 = Company()
        var comp2 = Company()
        comp1.name = "テスト1"
        comp2.name = "テスト2"
        CompanyDao.insert(comp1)
        CompanyDao.insert(comp2)

        CompanyDao.findAll().subscribe (
            { list -> list.forEach { v -> println("取得した値=" + v.name) }},
            { throwable -> println("エラーです。" + throwable.message) }
        )
    }
}