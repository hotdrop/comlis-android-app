package jp.hotdrop.compl.repository

import android.support.test.runner.AndroidJUnit4
import android.util.Log
import jp.hotdrop.compl.repository.company.CompanyRemoteDataSource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyRemoteDataSourceTest {

    private lateinit var remoteDataSource: CompanyRemoteDataSource

    @Before
    fun setup() {
        val appClient = MockClient().create()
        remoteDataSource = CompanyRemoteDataSource(appClient)
    }

    @Test
    fun findTest() {
        remoteDataSource.findAll().test().run {
            assertNoErrors()
            values().toList().forEach { Log.d("REMOTE ACCESS TEST", "get:" + it.toString() ) }
        }
    }
}