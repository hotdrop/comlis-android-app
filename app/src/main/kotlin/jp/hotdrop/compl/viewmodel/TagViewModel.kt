package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.compl.model.Tag

class TagViewModel(var tag: Tag, val context: Context): ViewModel() {

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = tag.name
    // TODO
    var attachCount = 0

    @ColorRes
    fun getColorRes(): Int {
        return ContextCompat.getColor(context, tag.colorRes)
    }

    fun change(vm: TagViewModel) {
        this.tag = vm.tag
        viewName = vm.viewName
        attachCount = vm.attachCount
    }

    override fun equals(other: Any?): Boolean {
        return (other as TagViewModel).tag.id == tag.id || super.equals(other)
    }

    fun MakeTag(): Tag = tag.apply {
        name = viewName
    }
}