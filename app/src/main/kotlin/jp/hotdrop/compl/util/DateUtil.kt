package jp.hotdrop.compl.util

import android.text.format.DateFormat
import java.util.*

object DateUtil {

    fun getNowDate(): Date {
        return Date(System.currentTimeMillis())
    }

    fun format(argDate: Date?): String? {
        argDate ?: return null
        return DateFormat.format("yyyy-MM-dd, E, HH:mm:ss", argDate).toString()
    }
}