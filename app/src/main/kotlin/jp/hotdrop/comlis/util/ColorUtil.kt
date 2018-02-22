package jp.hotdrop.comlis.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.ContextCompat
import jp.hotdrop.comlis.R

object ColorUtil {

    // この定数名は「val one = 1」と同じで全く意味ないが、値の方をそのままDBに保持している。
    // 最初はenumにしようとしたがselectでデータの中身を直接みたとき、この定義と一緒じゃないと全く分からなかったのでこうした。
    const val BLUE_NAME = "ブルー"
    const val RED_NAME = "レッド"
    const val YELLOW_NAME = "イエロー"
    const val GREEN_NAME = "グリーン"
    const val PURPLE_NAME = "パープル"

    private var colorMap = mutableMapOf<String, ColorData>()

    init {
        colorMap[BLUE_NAME] = ColorData(R.color.blue, R.color.light_blue, R.color.dark_blue, R.color.transparent_light_blue)
        colorMap[RED_NAME] = ColorData(R.color.red, R.color.light_red, R.color.dark_red, R.color.transparent_light_red)
        colorMap[YELLOW_NAME] = ColorData(R.color.yellow, R.color.light_yellow, R.color.dark_yellow, R.color.transparent_light_yellow)
        colorMap[GREEN_NAME] = ColorData(R.color.green, R.color.light_green, R.color.dark_green, R.color.transparent_light_green)
        colorMap[PURPLE_NAME] = ColorData(R.color.purple, R.color.light_purple, R.color.dark_purple, R.color.transparent_light_purple)
    }

    fun getNames() = colorMap.keys.toList()

    @ColorInt
    fun getResNormal(name: String, context: Context) =
            ContextCompat.getColor(context, colorMap[name]!!.normal)

    @ColorInt
    fun getResDark(name: String, context: Context) =
            ContextCompat.getColor(context, colorMap[name]!!.dark)

    @ColorInt
    fun getResLight(name: String, context: Context) =
            ContextCompat.getColor(context, colorMap[name]!!.light)

    @ColorInt
    fun getResTransparent(name: String, context: Context) =
            ContextCompat.getColor(context, colorMap[name]!!.transparent)

    fun getImageCover(name: String, context: Context): Drawable {
        when(name) {
            ColorUtil.BLUE_NAME ->  ContextCompat.getDrawable(context, R.drawable.blue_cover)
            ColorUtil.GREEN_NAME -> ContextCompat.getDrawable(context, R.drawable.green_cover)
            ColorUtil.RED_NAME -> ContextCompat.getDrawable(context, R.drawable.red_cover)
            ColorUtil.YELLOW_NAME -> ContextCompat.getDrawable(context, R.drawable.yellow_cover)
            ColorUtil.PURPLE_NAME -> ContextCompat.getDrawable(context, R.drawable.purple_cover)
            else -> null
        }?.let {
            return it
        }
        throw IllegalStateException("Unsupported Color name $name ")
    }

    data class ColorData(val normal: Int, val light: Int, val dark: Int, val transparent: Int)
}
