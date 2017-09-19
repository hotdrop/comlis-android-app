package jp.hotdrop.comlis.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.comlis.R
import jp.hotdrop.comlis.model.Tag
import jp.hotdrop.comlis.model.TagAssociateState
import jp.hotdrop.comlis.util.ColorUtil

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