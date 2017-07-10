package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class TagViewModel(var tag: Tag,
                   var attachCompanyCount: Int,
                   private val context: Context): ViewModel() {

    var viewName = tag.name

    override fun equals(other: Any?) =
            ((other as TagViewModel).getId() == tag.id || super.equals(other))

    fun isAttachCompany() = attachCompanyCount > 0

    @ColorRes
    fun getColorRes() = ColorUtil.getResLight(tag.colorType, context)

    fun getColorType() = tag.colorType

    fun getId() = tag.id

    // TextViewにバインドしているので文字列にする
    fun getAttachCompanyCountToString() = attachCompanyCount.toString()

    fun change(vm: TagViewModel) {
        this.tag = vm.tag
        viewName = vm.viewName
        attachCompanyCount = vm.attachCompanyCount
    }

}