package jp.hotdrop.comlis.product

import jp.hotdrop.comlis.model.ReceiveCompany
import jp.hotdrop.comlis.repository.company.CompanyRemoteDataSource
import org.junit.Before
import org.junit.Test

class RemoteConnectTest {

    private lateinit var remoteDataSource: CompanyRemoteDataSource

    @Before
    fun setup() {
        remoteDataSource = CompanyRemoteDataSource(ComlisClient().service())
    }

    /**
     * Precondition
     *  1. Comlis-data-store is running.
     *  2. Some data is saved in the comlis-data-store.
     */
    @Test
    fun connectTest() {
        var maxDateEpoch = 0L

        println(" First call findAll.")
        remoteDataSource.findAll(0).test().run {
            assertNoErrors()
            val receiveCompanies = values().first()
            checkReceiveCompany(receiveCompanies)
            maxDateEpoch = receiveCompanies.maxBy { it.dateEpoch }!!.dateEpoch
            assertComplete()
        }

        println(" Second call findAll.")
        remoteDataSource.findAll(maxDateEpoch).test().run {
            assertNoErrors()
            val receiveCompanies = values().first()
            checkReceiveCompany(receiveCompanies)
            assertComplete()
        }
    }

    private fun checkReceiveCompany(receiveCompanies: List<ReceiveCompany>) {
        if(receiveCompanies.isEmpty()) {
            println(" receiveCompanies are nothing.")
        }
        receiveCompanies.forEach {
            println("  ${it.name} 従業員数: ${it.employeesNum} 概要: ${it.overview} 日付: ${it.dateEpoch}" )
        }
    }
}