package jp.hotdrop.compl.repository

import android.content.Context
import android.support.test.InstrumentationRegistry
import jp.hotdrop.compl.model.Company
import jp.hotdrop.compl.model.OrmaDatabase
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.repository.company.CompanyLocalDataSource
import jp.hotdrop.compl.repository.company.CompanyRemoteDataSource
import jp.hotdrop.compl.repository.company.CompanyRepository
import jp.hotdrop.compl.repository.company.JobEvaluationLocalDataSource
import jp.hotdrop.compl.repository.tag.TagLocalDataSource
import jp.hotdrop.compl.repository.tag.TagRepository
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(shadows = arrayOf(MockClient.MyNetworkSecurityPolicy::class), sdk = intArrayOf(23))
class TagRepositoryTest {

    private lateinit var tagRepository: TagRepository
    private lateinit var companyRepository: CompanyRepository

    private fun getContext(): Context =
            InstrumentationRegistry.getTargetContext()

    @Before
    fun setup() {
        val orma = OrmaDatabase.builder(getContext()).name(null).build()
        val ormaHolder = OrmaHolder(orma)

        val localDataSource = TagLocalDataSource(ormaHolder)
        tagRepository = TagRepository(localDataSource)
        val companyLocalDataSource = CompanyLocalDataSource(ormaHolder, tagRepository)

        val appClient = MockClient().create()
        val remoteDataSource = CompanyRemoteDataSource(appClient)

        val jobEvaluateDataSource = JobEvaluationLocalDataSource(ormaHolder)

        companyRepository = CompanyRepository(companyLocalDataSource, remoteDataSource, jobEvaluateDataSource)
    }

    @Test
    fun findTest() {
        val tagName = "test1"
        val tag = createTag(tagName)
        tagRepository.insert(tag)

        val tagFromDb = tagRepository.find(tagName)!!
        assertCompareTag(tag, tagFromDb)
    }

    @Test
    fun findInIdTest() {

        val tagNames = mutableListOf("test1", "test2", "test3", "test4", "test5")
        tagNames.forEach {
            tagRepository.insert(createTag(it))
        }

        // 一部のIDのみ照合したいので偶数だけ取り出す。
        val ids = tagRepository.findAll()
                .blockingGet()
                .toList()
                .filter { it.id%2 == 0 }
                .map { it.id }
        assert(ids.size == 3)

        tagRepository.findInIds(ids).forEach {
            assert(tagNames.contains(it.name))
        }
    }

    @Test
    fun countByAttachCompanyTest() {

        val noAttachTagName = "tag1"
        val oneAttachTagName = "tag2"
        val threeAttachTagName = "tag3"
        companyRepository.insert(createCompany("test1"))
        companyRepository.insert(createCompany("test2"))
        companyRepository.insert(createCompany("test3"))
        companyRepository.insert(createCompany("test4"))
        mutableListOf(createTag(noAttachTagName), createTag(oneAttachTagName), createTag(threeAttachTagName))
                .forEach { tagRepository.insert(it) }

        val companies = companyRepository.findAll().blockingGet().toList()

        assert(tagRepository.find(noAttachTagName) == null)

        tagRepository.find(oneAttachTagName)?.also { tag ->
            companies.take(1).forEach {
                companyRepository.associateTagByCompany(it.id, mutableListOf(tag))
            }
            assert(tagRepository.countByAttachCompany(tag) == 1)
        }

        tagRepository.find(threeAttachTagName)?.also { tag ->
            companies.takeLast(3).forEach {
                companyRepository.associateTagByCompany(it.id, mutableListOf(tag))
            }
            assert(tagRepository.countByAttachCompany(tag) == 3)
        }
    }

    @Test
    fun insertTest() {
        val tagName1 = "test1"
        val tagName2 = "test2"
        val tag1 = createTag(tagName1)
        val tag2 = createTag(tagName2)
        tagRepository.insert(tag1)
        tagRepository.insert(tag2)

        val tag1FromDb = tagRepository.find(tagName1)!!
        val tag2FromDb = tagRepository.find(tagName2)!!
        assertCompareTag(tag1, tag1FromDb)
        assertCompareTag(tag2, tag2FromDb)
        assert(tag1FromDb.viewOrder == 1)
        assert(tag2FromDb.viewOrder == 2)
    }

    @Test
    fun updateTest() {
        val tagName = "insert"
        tagRepository.insert(createTag(tagName, "firstColor"))
        val tagFromDb = tagRepository.find(tagName)!!

        val tagUpdate = createTag("update", "secondColor").apply {
            id =  tagFromDb.id
            registerDate = tagFromDb.registerDate
            viewOrder = 5
        }
        tagRepository.update(tagUpdate)

        val tagUpdated = tagRepository.find(tagUpdate.name)!!
        assertCompareTag(tagUpdate, tagUpdated)
        assert(tagUpdated.viewOrder == 5)
    }

    @Test
    fun updateAllOrderTest() {
        mutableListOf(createTag("order 1 To 3 "), createTag("order 2 To 1 "), createTag("order 3 TO 2 "))
                .forEach { tagRepository.insert(it) }
        val tagsFromDb = tagRepository.findAll().blockingGet().toList()
        val makeReorderTags = mutableListOf(tagsFromDb[1], tagsFromDb[2], tagsFromDb[0])
        tagRepository.updateAllOrder(makeReorderTags)

        val tagsFromDbAtReordered = tagRepository.findAll().blockingGet().toList()
        makeReorderTags.forEachIndexed { index, tag ->
            assertCompareTag(tag, tagsFromDbAtReordered[index])
        }
    }

    @Test
    fun deleteTest() {
        val t1 = createTag("delete1")
        val t2 = createTag("delete2")
        tagRepository.insert(t1)
        tagRepository.insert(t2)

        tagRepository.delete(t1)
        assert(!tagRepository.exist("delete1"))
        tagRepository.delete(t2)
        assert(!tagRepository.exist("delete2"))
    }

    @Test
    fun existTest() {
        val tagNames = mutableListOf("test1", "test2", "test3", "test4", "test5")
        tagNames.forEach { tagRepository.insert(createTag(it)) }
        tagNames.forEach { tagName ->
            assert(tagRepository.exist(tagName))
        }
    }

    @Test
    fun existExclusionIdTest() {
        tagRepository.insert(createTag("ownName"))
        tagRepository.insert(createTag("sameName"))

        // ownNameとsameNameはフィールドからの入力文字列を想定しているので変数ではなくいちいち文字列を直指定しています。
        val tagsFromDb = tagRepository.find("ownName")!!
        val isSuccess = tagRepository.existExclusionId("ownName", tagsFromDb.id)
        assert(isSuccess)
        val isFailure = tagRepository.existExclusionId("sameName", tagsFromDb.id)
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

    private fun createCompany(argName: String) = Company().apply {
        name = argName
        categoryId = 1
    }
}