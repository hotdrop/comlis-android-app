package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject

class TagsViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var tagDao: TagDao

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun getData(): Single<List<TagViewModel>> {
        return tagDao.findAll()
                .map { tags ->
                    tags.map {
                        val attachCnt = tagDao.countByAttachCompany(it)
                        TagViewModel(it, attachCnt, context)
                    }
                }
    }

    fun existName(name: String): Boolean {
        return tagDao.exist(name)
    }

    fun existNameExclusionId(name: String, id: Int): Boolean {
        return tagDao.existExclusionId(name, id)
    }

    fun getViewModel(name: String): TagViewModel {
        val tag = tagDao.find(name)
        val attachCnt = tagDao.countByAttachCompany(tag)
        return TagViewModel(tag, attachCnt, context)
    }

    fun register(name: String, colorType: String) {
        tagDao.insert(Tag().apply {
            this.name = name
            this.colorType = colorType
        })
    }

    fun update(vm: TagViewModel, newName: String, newColorType: String) {
        val t = vm.tag.apply {
            name = newName
            colorType = newColorType
        }
        tagDao.update(t)
    }

    fun updateItemOrder(tags: List<Tag>) {
        tagDao.updateAllOrder(tags)
    }

    fun delete(vm: TagViewModel) {
        tagDao.delete(vm.tag)
    }

    fun visibilityEmptyMessageOnScreen() {
        emptyMessageVisibility = View.VISIBLE
    }
    fun goneEmptyMessageOnScreen() {
        emptyMessageVisibility = View.GONE
    }
}
