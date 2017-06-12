package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class TagViewModel(var tag: Tag,
                   var attachCompanyCount: Int,
                   private val context: Context): ViewModel() {

    var viewName = tag.name

    override fun equals(other: Any?): Boolean = ((other as TagViewModel).tag.id == tag.id || super.equals(other))

    fun isAttachCompany(): Boolean = attachCompanyCount > 0

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResLight(tag.colorType, context)

    fun getId() = tag.id

    fun getColorType() = tag.colorType

    // TextViewにバインドしているので文字列にする
    fun getAttachCompanyCountToString() = attachCompanyCount.toString()

    fun change(vm: TagViewModel) {
        this.tag = vm.tag
        viewName = vm.viewName
        attachCompanyCount = vm.attachCompanyCount
    }

}