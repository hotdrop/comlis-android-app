package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.compl.R
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class TagAssociateViewModel(var tag: Tag, val context: Context, var isAttach: Boolean): ViewModel() {

    // 画面表示に使うデータだけmodelとは別にフィールド値を持たせる
    var viewName = tag.name

    fun changeAttachState() {
        isAttach = !isAttach
    }

    @ColorRes
    fun getColorRes(): Int {
        return if(isAttach) {
            ColorUtil.getResLight(tag.colorType, context)
        } else {
            ColorUtil.getResTransparent(tag.colorType, context)
        }
    }

    @ColorRes
    fun getBackGroundColorRes(): Int {
        return if(isAttach) {
            ContextCompat.getColor(context, R.color.item_tag_background)
        } else {
            ContextCompat.getColor(context, R.color.item_tag_unselected_background)
        }
    }
}