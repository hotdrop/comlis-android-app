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
        CompanyDao.insert(Company(name="テスト1", content = "テストです。", category = null))
        CompanyDao.insert(Company(name="テスト2", content = "テストです。", category = null))

        CompanyDao.findAll().subscribe {
            list -> list.forEach { v ->
            //Assert.assertTrue(v.name.equals("テスト"))
            print(" 取得した値=" + v.name)
        }}
    }
}