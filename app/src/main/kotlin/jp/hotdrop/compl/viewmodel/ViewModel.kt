package jp.hotdrop.compl.viewmodel

import android.text.format.DateFormat
import java.util.*

abstract class ViewModel {

    fun String.isNumber(): Boolean {
        this.forEach { c -> if(!c.isDigit()) return false }
        return true
    }

    fun String.toStringOrNull(): String? {
        return if(!this.isEmpty()) this else null
    }

    fun String.toIntOrZero(): Int {
        return if(!this.isEmpty()) this.toInt() else 0
    }

    fun Date.format(): String? {
        return DateFormat.format("yyyy-MM-dd(E) HH:mm:ss", this).toString()
    }
}