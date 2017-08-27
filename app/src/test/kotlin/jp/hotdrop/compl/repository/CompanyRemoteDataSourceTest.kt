package jp.hotdrop.compl.repository

import android.security.NetworkSecurityPolicy
import jp.hotdrop.compl.repository.company.CompanyRemoteDataSource
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements

@RunWith(RobolectricTestRunner::class)
@Config(shadows = arrayOf(CompanyRemoteDataSourceTest.MyNetworkSecurityPolicy::class), sdk = intArrayOf(23))
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

    /**
     * Test Mock Server is http. not good...
     */
    @Implements(NetworkSecurityPolicy::class)
    class MyNetworkSecurityPolicy {

        companion object {
            @Implementation
            @JvmStatic fun getInstance(): NetworkSecurityPolicy {
                val shadow = MyNetworkSecurityPolicy::class.java.classLoader.loadClass("android.security.NetworkSecurityPolicy")
                return (shadow.newInstance() as NetworkSecurityPolicy)
            }
        }

        @Implementation
        fun isCleartextTrafficPermitted() = true
    }
}