package jp.hotdrop.compl.viewmodel

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.compl.R
import jp.hotdrop.compl.dao.CompanyDao
import jp.hotdrop.compl.model.Tag
import jp.hotdrop.compl.util.ColorUtil

class TagAssociateViewModel(val companyId: Int = -1, var tag: Tag, val context: Context): ViewModel() {

    var viewName = tag.name
    var isAssociate = if(companyId != -1) CompanyDao.hasAssociateTag(companyId, tag.id) else true

    fun changeAssociateState() {
        isAssociate = !isAssociate
    }

    @ColorRes
    fun getColorRes(): Int {
        return if(isAssociate) {
            ColorUtil.getResLight(tag.colorType, context)
        } else {
            ColorUtil.getResTransparent(tag.colorType, context)
        }
    }

    @ColorRes
    fun getBackGroundColorRes(): Int {
        return if(isAssociate) {
            ContextCompat.getColor(context, R.color.item_tag_background)
        } else {
            ContextCompat.getColor(context, R.color.item_tag_unselected_background)
        }
    }
}