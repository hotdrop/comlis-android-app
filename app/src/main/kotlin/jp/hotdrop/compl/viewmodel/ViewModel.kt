package jp.hotdrop.compl.viewmodel

import android.text.format.DateFormat
import java.util.*

abstract class ViewModel {

    fun String.isNumber(): Boolean {
        this.forEach { c -> if(!c.isDigit()) return false }
        return true
    }

    fun Date.now(): Date {
        return Date(System.currentTimeMillis())
    }

    fun Date.format(): String? {
        return DateFormat.format("yyyy-MM-dd, E, HH:mm:ss", this).toString()
    }

    data class ErrorMessage(val message: String?)
}