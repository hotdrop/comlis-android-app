package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.dao.TagDao
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class TagViewModel(var tag: Tag, val context: Context): ViewModel() {

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = tag.name
    var attachCount = TagDao.countByAttachCompany(tag).toString()

    @ColorRes
    fun getColorRes(): Int {
        return ColorUtil.getResLight(tag.colorType, context)
    }

    fun change(vm: TagViewModel) {
        this.tag = vm.tag
        viewName = vm.viewName
        attachCount = vm.attachCount
    }

    override fun equals(other: Any?): Boolean {
        return (other as TagViewModel).tag.id == tag.id || super.equals(other)
    }

    fun makeTag(): Tag = tag.apply {
        name = viewName
    }
}