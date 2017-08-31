package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.compl.R
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.model.TagAssociateState
import jp.hotdrop.compl.util.ColorUtil

class TagAssociateViewModel(
        var tag: Tag,
        private var tagAssociateState: TagAssociateState,
        val context: Context
): ViewModel() {

    var viewName = tag.name

    fun changeAssociateState() {
        tagAssociateState = tagAssociateState.flip()
    }

    @ColorRes
    fun getColorRes() = if(tagAssociateState == TagAssociateState.ASSOCIATED) {
        ColorUtil.getResLight(tag.colorType, context)
    } else {
        ColorUtil.getResTransparent(tag.colorType, context)
    }

    @ColorRes
    fun getBackGroundColorRes() = if(tagAssociateState == TagAssociateState.ASSOCIATED) {
        ContextCompat.getColor(context, R.color.item_tag_background)
    } else {
        ContextCompat.getColor(context, R.color.item_tag_unselected_background)
    }

    fun isAssociated() = (tagAssociateState == TagAssociateState.ASSOCIATED)
}