package jp.hotdrop.compl.repository

import jp.hotdrop.compl.repository.company.CompanyRemoteDataSource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(shadows = arrayOf(MockClient.MyNetworkSecurityPolicy::class), sdk = intArrayOf(23))
class CompanyRemoteDataSourceTest {

    private lateinit var remoteDataSource: CompanyRemoteDataSource

    @Before
    fun setup() {
        remoteDataSource = CompanyRemoteDataSource(MockClient().create())
    }

    @Test
    fun connectTest() {
        println("Connect test start!")
        remoteDataSource.findAll().test().run {
            assertNoErrors()
            values().forEach { it.forEach { println("  " + it.name) } }
            assertComplete()
        }
    }
}