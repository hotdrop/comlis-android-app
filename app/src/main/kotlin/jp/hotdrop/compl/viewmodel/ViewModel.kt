package jp.hotdrop.compl.viewmodel

import android.databinding.BaseObservable
import android.text.format.DateFormat
import com.airbnb.lottie.LottieAnimationView
import java.util.*

abstract class ViewModel: BaseObservable() {

    fun String.isNumber(): Boolean {
        this.forEach { c -> if(!c.isDigit()) return false }
        return true
    }

    fun String.toStringOrNull(): String? = if(!this.isEmpty()) this else null

    fun String.toIntOrZero(): Int = if(!this.isEmpty()) this.toInt() else 0

    fun Date.format(): String? = DateFormat.format("yyyy-MM-dd(E) HH:mm:ss", this).toString()

    // RecyclerViewなどでViewModelを更新したい場合、元のキー（equalsで判定するID）は
    // 同じだが中身が変更されている場合の差し換え用拡張関数
    fun <T> MutableList<T>.extendReplace(n: T) {
        this.forEachIndexed { index, t ->
            if(t == n) {
                this[index] = n
            }
        }
    }

    // LottieAnimationViewの拡張関数がBaseFragmentとここで散在しているのがあまり良くないが
    // 無理やりまとめるのも良くないので今はこうする。
    fun LottieAnimationView.reset() {
        this.cancelAnimation()
        this.progress = 0.toFloat()
    }
}