package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.view.View
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import jp.hotdrop.compl.BR
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.model.Tag
import javax.inject.Inject

class TagsViewModel @Inject constructor(val context: Context): ViewModel() {

    @Inject
    lateinit var compositeDisposable: CompositeDisposable
    @Inject
    lateinit var tagDao: TagDao

    private var viewModels: ObservableList<TagViewModel> = ObservableArrayList()
    private lateinit var callback: Callback

    @get:Bindable
    var emptyMessageVisibility = View.GONE
        set(value) {
            field = value
            notifyPropertyChanged(BR.emptyMessageVisibility)
        }

    fun setCallBack(callback: Callback) {
        this.callback = callback
    }

    fun loadData() {
        val disposable = tagDao.findAll()
                .map { tags ->
                    tags.map {
                        val attachCnt = tagDao.countByAttachCompany(it)
                        TagViewModel(it, attachCnt, context)
                    }
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { onSuccess(it) },
                        { callback.showError(it) }
                )
        compositeDisposable.add(disposable)
    }

    private fun onSuccess(tagViewModels: List<TagViewModel>) {
        if(tagViewModels.isNotEmpty()) {
            viewModels.addAll(tagViewModels)
        }
        checkAndUpdateEmptyMessageVisibility()
    }

    fun destroy() {
        compositeDisposable.clear()
    }

    fun existName(name: String): Boolean {
        return tagDao.exist(name)
    }

    fun existNameExclusionId(name: String, id: Int): Boolean {
        return tagDao.existExclusionId(name, id)
    }

    fun register(name: String, colorType: String) {
        tagDao.insert(Tag().apply {
            this.name = name
            this.colorType = colorType
        })
        val tag = tagDao.find(name)
        val attachCnt = tagDao.countByAttachCompany(tag)
        viewModels.add(TagViewModel(tag, attachCnt, context))
        checkAndUpdateEmptyMessageVisibility()
    }

    fun update(vm: TagViewModel, newName: String, newColorType: String) {
        val t = vm.tag.apply {
            name = newName
            colorType = newColorType
        }
        tagDao.update(t)
        val idx = viewModels.indexOf(vm)
        viewModels[idx] = TagViewModel(t, vm.attachCount.toInt(), context)
    }

    fun updateItemOrder() {
        val tags = viewModels.map { it.tag }
        tagDao.updateAllOrder(tags)
    }

    fun delete(vm: TagViewModel) {
        viewModels.remove(vm)
        checkAndUpdateEmptyMessageVisibility()
    }

    private fun checkAndUpdateEmptyMessageVisibility() {
        if(viewModels.isNotEmpty()) {
            emptyMessageVisibility = View.GONE
        } else {
            emptyMessageVisibility = View.VISIBLE
        }
    }

    interface Callback {
        fun showError(throwable: Throwable)
    }

}