package jp.hotdrop.compl.util

import android.support.annotation.ColorRes
import jp.hotdrop.compl.R

object ColorDataUtil {

    private var colorMap = mutableMapOf<String, ColorData>()

    init {
        colorMap.put("ブルー", ColorData(R.color.blue, R.color.light_blue))
        colorMap.put("レッド", ColorData(R.color.red, R.color.light_red))
        colorMap.put("イエロー", ColorData(R.color.yellow, R.color.light_yellow))
        colorMap.put("グリーン", ColorData(R.color.green, R.color.light_green))
        colorMap.put("パープル", ColorData(R.color.purple, R.color.light_purple))
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
}

data class ColorData(val normal: Int, val light: Int)