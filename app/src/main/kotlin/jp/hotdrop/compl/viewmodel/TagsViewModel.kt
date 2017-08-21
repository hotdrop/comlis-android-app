package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.view.View
import io.reactivex.Single
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.repository.tag.TagRepository
import javax.inject.Inject

class TagsViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var tagRepository: TagRepository

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun getData(): Single<List<TagViewModel>> =
            tagRepository.findAll()
                    .map { tags ->
                        tags.map {
                            val attachCnt = tagRepository.countByAttachCompany(it)
                            TagViewModel(it, attachCnt, context)
                        }
                    }

    fun existName(name: String) =
            tagRepository.exist(name)

    fun existNameExclusionId(name: String, id: Int) =
            tagRepository.existExclusionId(name, id)

    fun register(name: String, colorType: String) {
        tagRepository.insert(Tag().apply {
            this.name = name
            this.colorType = colorType
        })
    }

    fun update(vm: TagViewModel, newName: String, newColorType: String) {
        val t = vm.tag.apply {
            name = newName
            colorType = newColorType
        }
        tagRepository.update(t)
    }

    fun getViewModel(name: String): TagViewModel? =
            tagRepository.find(name)?.let {
            val attachCnt = tagRepository.countByAttachCompany(it)
            TagViewModel(it, attachCnt, context)
        }

    fun updateItemOrder(tags: List<Tag>) {
        tagRepository.updateAllOrder(tags)
    }

    fun delete(vm: TagViewModel) {
        tagRepository.delete(vm.tag)
    }

    fun visibilityEmptyMessageOnScreen() {
        emptyMessageVisibility = View.VISIBLE
    }
    fun goneEmptyMessageOnScreen() {
        emptyMessageVisibility = View.GONE
    }
}
