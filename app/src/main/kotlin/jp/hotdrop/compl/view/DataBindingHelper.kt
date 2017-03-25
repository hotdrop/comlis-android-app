package jp.hotdrop.compl.view

import android.databinding.BindingAdapter
import android.widget.ImageView
import jp.hotdrop.compl.R
import jp.hotdrop.compl.util.ColorUtil

object DataBindingHelper {

    @BindingAdapter("imageCover")
    @JvmStatic
    fun setImageCover(imageView: ImageView, colorName: String) {
        when(colorName) {
            ColorUtil.BLUE_NAME -> imageView.setImageResource(R.drawable.blue_cover)
            ColorUtil.GREEN_NAME -> imageView.setImageResource(R.drawable.green_cover)
            ColorUtil.RED_NAME -> imageView.setImageResource(R.drawable.red_cover)
            ColorUtil.YELLOW_NAME -> imageView.setImageResource(R.drawable.yellow_cover)
            ColorUtil.PURPLE_NAME -> imageView.setImageResource(R.drawable.purple_cover)
        }
    }
}