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
    fun findTest() {
        val tagName = "test1"
        val tag = createTag(tagName)
        tagDao.insert(tag)

        val tagFromDb = tagDao.find(tagName)
        assertCompareTag(tag, tagFromDb)
    }

    @Test
    fun findInIdTest() {
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

    @Test
    fun insertTest() {
        val tagName1 = "test1"
        val tagName2 = "test2"
        val tag1 = createTag(tagName1)
        val tag2 = createTag(tagName2)
        tagDao.insert(tag1)
        tagDao.insert(tag2)

        val tag1FromDb = tagDao.find(tagName1)
        val tag2FromDb = tagDao.find(tagName2)
        assertCompareTag(tag1, tag1FromDb)
        assertCompareTag(tag2, tag2FromDb)
        assert(tag1FromDb.viewOrder == 1)
        assert(tag2FromDb.viewOrder == 2)
    }

    @Test
    fun updateTest() {
        val tagName = "insert"
        tagDao.insert(createTag(tagName, "firstColor"))
        val tagFromDb = tagDao.find(tagName)

        val tagUpdate = createTag("update", "secondColor").apply {
            id =  tagFromDb.id
            registerDate = tagFromDb.registerDate
            viewOrder = 5
        }
        tagDao.update(tagUpdate)

        val tagUpdated = tagDao.find(tagUpdate.name)
        assertCompareTag(tagUpdate, tagUpdated)
        assert(tagUpdated.viewOrder == 5)
    }

    @Test
    fun updateAllOrderTest() {
        mutableListOf(createTag("order 1 To 3 "), createTag("order 2 To 1 "), createTag("order 3 TO 2 ")).forEach { tagDao.insert(it) }
        val tagsFromDb = tagDao.findAll().blockingGet().toList()
        val makeReorderTags = mutableListOf(tagsFromDb[1], tagsFromDb[2], tagsFromDb[0])
        tagDao.updateAllOrder(makeReorderTags)

        val tagsFromDbAtReordered = tagDao.findAll().blockingGet().toList()
        makeReorderTags.forEachIndexed { index, tag ->
            assertCompareTag(tag, tagsFromDbAtReordered[index])
        }
    }

    @Test
    fun deleteTest() {
        val t1 = createTag("delete1")
        val t2 = createTag("delete2")
        tagDao.insert(t1)
        tagDao.insert(t2)

        tagDao.delete(t1)
        assert(!tagDao.exist("delete1"))
        tagDao.delete(t2)
        assert(!tagDao.exist("delete2"))
    }

    @Test
    fun existTest() {
        val tagNames = mutableListOf("test1", "test2", "test3", "test4", "test5")
        tagNames.forEach { tagDao.insert(createTag(it)) }
        tagNames.forEach { tagName ->
            assert(tagDao.exist(tagName))
        }
    }

    @Test
    fun existExclusionIdTest() {
        tagDao.insert(createTag("ownName"))
        tagDao.insert(createTag("sameName"))

        // ownNameとsameNameはフィールドからの入力文字列を想定しているので変数ではなくいちいち文字列を直指定しています。
        val tagsFromDb = tagDao.find("ownName")
        val isSuccess = tagDao.existExclusionId("ownName", tagsFromDb.id)
        assert(isSuccess)
        val isFailure = tagDao.existExclusionId("sameName", tagsFromDb.id)
        assert(!isFailure)
    }

    private fun createTag(argName: String, argColorType: String = "ブルー") = Tag().apply {
        name = argName
        colorType = argColorType
    }

    private fun assertCompareTag(t1: Tag, t2: Tag) {
        assert(t1.name == t2.name)
        assert(t1.colorType == t2.colorType)
        assert(t1.registerDate == t2.registerDate)
    }
}