package jp.hotdrop.comlis.repository

import jp.hotdrop.comlis.repository.company.CompanyRemoteDataSource
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(23), manifest = Config.NONE)
class CompanyRemoteDataSourceTest {

    @Test
    fun findAllTest() {

        val mockServer = MockComlisServer()

        // getUrlするとmockServerのstartが実行されるため実行中をstart→remoteDataSourceの順にしている。
        mockServer.startNormal()
        val remoteDataSource = CompanyRemoteDataSource(MockComlisClient(mockServer.getUrl()).service())

        remoteDataSource.findAll(0).test().run {
            assertNoErrors()
            val receiveCompanies = values().first()
            receiveCompanies.forEach {
                println("  Test response data. name: ${it.name} 従業員数: ${it.employeesNum} " )
            }
            assert(receiveCompanies.size == 3)
            assertComplete()
        }
        mockServer.stop()
    }
}
