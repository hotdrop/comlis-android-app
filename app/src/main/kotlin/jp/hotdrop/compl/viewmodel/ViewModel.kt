package jp.hotdrop.compl.viewmodel

import android.databinding.BaseObservable
import android.text.format.DateFormat
import java.util.*

abstract class ViewModel: BaseObservable() {

    fun String.toStringOrNull(): String? =
            if(!this.isEmpty())
                this
            else
                null

    fun String.toIntOrZero(): Int =
            if(!this.isEmpty())
                this.toInt()
            else
                0

    fun Date.format(): String? = DateFormat.format("yyyy-MM-dd(E) HH:mm:ss", this).toString()

}