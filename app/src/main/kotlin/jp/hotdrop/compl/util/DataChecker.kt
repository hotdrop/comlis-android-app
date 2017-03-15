package jp.hotdrop.compl.util

object DataChecker {

    fun isNumber(s: String): Boolean {
        s.forEach { c -> if(!c.isDigit()) return false }
        return true
    }
}