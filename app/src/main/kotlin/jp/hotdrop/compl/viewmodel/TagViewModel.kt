package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class TagViewModel(var tag: Tag,
                   private var attachCompanyCount: Int,
                   private val context: Context): ViewModel() {

    var viewName = tag.name

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

    override fun equals(other: Any?) =
            ((other as TagViewModel).getId() == tag.id || super.equals(other))

    override fun hashCode(): Int {
        var result = tag.hashCode()
        result = 31 * result + attachCompanyCount
        result = 31 * result + context.hashCode()
        result = 31 * result + viewName.hashCode()
        return result
    }

}