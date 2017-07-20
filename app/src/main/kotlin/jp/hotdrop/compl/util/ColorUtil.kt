package jp.hotdrop.compl.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import jp.hotdrop.compl.R

object ColorUtil {

    // この定数名は「val one = 1」と同じで全く意味ないが、値の方をそのままDBに保持している。
    // 最初はenumにしようとしたがselectでデータの中身を直接みたとき、この定義と一緒じゃないと全く分からなかったのでこうした。
    val BLUE_NAME = "ブルー"
    val RED_NAME = "レッド"
    val YELLOW_NAME = "イエロー"
    val GREEN_NAME = "グリーン"
    val PURPLE_NAME = "パープル"

    private var colorMap = mutableMapOf<String, ColorData>()

    init {
        colorMap.put(BLUE_NAME, ColorData(R.color.dark_blue, R.color.light_blue, R.color.dark_blue, R.color.transparent_light_blue))
        colorMap.put(RED_NAME, ColorData(R.color.red, R.color.light_red, R.color.dark_red, R.color.transparent_light_red))
        colorMap.put(YELLOW_NAME, ColorData(R.color.yellow, R.color.light_yellow, R.color.dark_yellow, R.color.transparent_light_yellow))
        colorMap.put(GREEN_NAME, ColorData(R.color.green, R.color.light_green, R.color.dark_green, R.color.transparent_light_green))
        colorMap.put(PURPLE_NAME, ColorData(R.color.purple, R.color.light_purple, R.color.dark_purple, R.color.transparent_light_purple))
    }

    fun getNames(): List<String> {
        return colorMap.keys.toList()
    }

    @ColorRes
    fun getResNormal(name: String, context: Context): Int {
        return ContextCompat.getColor(context, colorMap[name]!!.normal)
    }

    @ColorRes
    fun getResDark(name: String, context: Context): Int {
        return ContextCompat.getColor(context, colorMap[name]!!.dark)
    }

    @ColorRes
    fun getResLight(name: String, context: Context): Int {
        return ContextCompat.getColor(context, colorMap[name]!!.light)
    }

    @ColorRes
    fun getResTransparent(name: String, context: Context): Int {
        return ContextCompat.getColor(context, colorMap[name]!!.transparent)
    }

    fun getImageCover(name: String, context: Context): Drawable {
        when(name) {
            ColorUtil.BLUE_NAME ->  return ContextCompat.getDrawable(context, R.drawable.blue_cover)
            ColorUtil.GREEN_NAME -> return ContextCompat.getDrawable(context, R.drawable.green_cover)
            ColorUtil.RED_NAME -> return ContextCompat.getDrawable(context, R.drawable.red_cover)
            ColorUtil.YELLOW_NAME -> return ContextCompat.getDrawable(context, R.drawable.yellow_cover)
            ColorUtil.PURPLE_NAME -> return ContextCompat.getDrawable(context, R.drawable.purple_cover)
        }
        throw IllegalStateException("Unsupported Color name $name ")
    }

    data class ColorData(val normal: Int, val light: Int, val dark: Int, val transparent: Int)
}
