package jp.hotdrop.compl.repository.tag

import io.reactivex.Single
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TagRepository @Inject constructor(private val localRepository: TagLocalDataSource) {

    fun find(name: String): Tag? =
            localRepository.find(name)

    fun findInIds(ids: List<Int>): List<Tag> =
            localRepository.findInIds(ids)

    fun findAll(): Single<List<Tag>> =
            localRepository.findAll()

    fun countByAttachCompany(tag: Tag) =
            localRepository.countByAttachCompany(tag)

    fun insert(tag: Tag) {
        localRepository.insert(tag)
    }

    fun update(tag: Tag) {
        localRepository.update(tag)
    }

    fun updateAllOrder(tags: List<Tag>) {
        localRepository.updateAllOrder(tags)
    }

    fun delete(tag: Tag) {
        localRepository.delete(tag)
    }

    fun exist(name: String) =
            localRepository.exist(name)

    fun existExclusionId(name: String, id: Int) =
            localRepository.existExclusionId(name, id)

}