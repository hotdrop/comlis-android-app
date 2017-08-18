package jp.hotdrop.compl.view.customlayout

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.GridLayoutAnimationController
import com.google.android.flexbox.FlexboxLayoutManager

class CustomRecyclerView: RecyclerView {

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int): super(context, attributeSet, defStyle)

    override fun attachLayoutAnimationParameters(child: View?, params: ViewGroup.LayoutParams?, index: Int, count: Int) {
        super.attachLayoutAnimationParameters(child, params, index, count)

        val flexboxLayoutManager = layoutManager as? FlexboxLayoutManager
        if(adapter != null && flexboxLayoutManager != null) {
            var animParams = params?.layoutAnimationParameters as? GridLayoutAnimationController.AnimationParameters
            if(animParams == null) {
               animParams = GridLayoutAnimationController.AnimationParameters()
               params?.layoutAnimationParameters = animParams
            }

            animParams.count = count
            animParams.index = index

            val columns = flexboxLayoutManager.flexLines.size
            animParams.columnsCount = columns
            animParams.rowsCount = count / columns

            val invertedIndex = count - 1 - index
            animParams.column = columns - 1 - (invertedIndex % columns)
            animParams.row = animParams.rowsCount - 1 - invertedIndex / columns

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count)
        }
    }
}