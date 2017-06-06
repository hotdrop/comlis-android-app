package jp.hotdrop.compl.dao

import android.content.Context
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import jp.hotdrop.compl.model.OrmaDatabase
import jp.hotdrop.compl.model.Tag
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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

    @Test
    fun insertAndFindTest() {
        val tagName = "test1"
        val tag = createTag(tagName)
        tagDao.insert(tag)

        val tagFromDb = tagDao.find(tagName)
        assertCompareTag(tag, tagFromDb)
    }

    @Test
    fun findAllAndInIdTest() {
        val tagNames = mutableListOf("test1", "test2", "test3", "test4", "test5")
        tagNames.forEach { tagDao.insert(createTag(it)) }
        val tags = tagDao.findAll().blockingGet().toList()
        val ids = tags.filter{ tag -> tag.id%2 == 0 }.map { it.id }
        val tagByEvenNumberId = tagDao.findInId(ids)
        assert(ids.size == 3)
        tagByEvenNumberId.forEach { tag ->
            assert(tagNames.contains(tag.name))
        }
    }

    fun countByAttachCompanyTest() {
    }

    fun updateTest() {
    }

    fun updateAllOrderTest() {

    }

    fun deleteTest() {
    }

    fun existTest() {

    }

    fun existExclusionIdTest() {
    }

    private fun createTag(argName: String) = Tag().apply {
        name = argName
        colorType = "ブルー"
    }

    private fun assertCompareTag(t1: Tag, t2: Tag) {
        assert(t1.name == t2.name)
        assert(t1.colorType == t2.colorType)
        assert(t1.registerDate == t2.registerDate)
    }
}