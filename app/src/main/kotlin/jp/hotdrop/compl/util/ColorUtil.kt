package jp.hotdrop.compl.util

import android.support.annotation.ColorRes
import jp.hotdrop.compl.R

object ColorUtil {

    val BLUE_NAME = "ブルー"
    val RED_NAME = "レッド"
    val YELLOW_NAME = "イエロー"
    val GREEN_NAME = "グリーン"
    val PURPLE_NAME = "パープル"

    private var colorMap = mutableMapOf<String, ColorData>()

    init {
        colorMap.put(BLUE_NAME, ColorData(R.color.blue, R.color.light_blue))
        colorMap.put(RED_NAME, ColorData(R.color.red, R.color.light_red))
        colorMap.put(YELLOW_NAME, ColorData(R.color.yellow, R.color.light_yellow))
        colorMap.put(GREEN_NAME, ColorData(R.color.green, R.color.light_green))
        colorMap.put(PURPLE_NAME, ColorData(R.color.purple, R.color.light_purple))
    }

    fun getNames(): List<String> {
        return colorMap.keys.toList()
    }

    @ColorRes
    fun getColorNormal(name: String): Int {
        return colorMap[name]!!.normal
    }

    @ColorRes
    fun getColorLight(name: String): Int {
        return colorMap[name]!!.light
    }

    data class ColorData(val normal: Int, val light: Int)
}
