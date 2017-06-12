package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class TagViewModel(var tag: Tag,
                   attachCompanyCount: Int,
                   val context: Context): ViewModel() {

    var viewName = tag.name
    // TextViewにバインドしているので文字列にする
    var attachCount = attachCompanyCount.toString()

    @ColorRes
    fun getColorRes(): Int = ColorUtil.getResLight(tag.colorType, context)
    fun getId() = tag.id
    fun getColorType() = tag.colorType

    override fun equals(other: Any?): Boolean = ((other as TagViewModel).tag.id == tag.id || super.equals(other))

    fun isAttachCompany(): Boolean {
        return attachCount.toInt() > 0
    }
}