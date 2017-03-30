package jp.hotdrop.compl.viewmodel

abstract class ViewModel {

    fun String.isNumber(): Boolean {
        forEach { c -> if(!c.isDigit()) return false }
        return true
    }
}