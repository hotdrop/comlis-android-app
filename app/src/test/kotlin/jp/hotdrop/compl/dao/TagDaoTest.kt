package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import jp.hotdrop.compl.model.OrmaDatabase
import org.junit.Before

class TagDaoTest {

    private lateinit var orma: OrmaDatabase

    private fun getContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    @Before
    fun setup() {
        OrmaHolder.initialize(getContext(), false)
        orma = OrmaHolder.buildDB
    }


}