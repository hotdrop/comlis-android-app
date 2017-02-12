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
    private lateinit var dao: CompanyDao


    private fun getContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    @Before
    fun setup() {
        orma = OrmaDatabase.builder(getContext()).name(null).build()
        dao = CompanyDao(orma)
    }

    @Test
    fun insertData() {
        dao.insert(Company(name="テスト1", content = "テストです。"))
        dao.insert(Company(name="テスト2", content = "テストです。"))

        dao.findAll().subscribe {
            list -> list.forEach { v ->
            //Assert.assertTrue(v.name.equals("テスト"))
            print(" 取得した値=" + v.name)
        }}
    }
}