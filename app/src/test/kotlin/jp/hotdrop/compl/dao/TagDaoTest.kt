package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import jp.hotdrop.compl.model.OrmaDatabase
import org.junit.Before

class TagDaoTest {

    private lateinit var tagDao: TagDao

    private fun getContext(): Context {
        return InstrumentationRegistry.getTargetContext()
    }

    @Before
    fun setup() {
        val orma = OrmaDatabase.builder(getContext()).name(null).build()
        val ormaHolder = OrmaHolder(orma)
        tagDao = TagDao(ormaHolder)
    }
}