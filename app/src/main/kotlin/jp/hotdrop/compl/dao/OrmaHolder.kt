package jp.hotdrop.compl.dao

import android.content.Context
import com.github.gfx.android.orma.AccessThreadConstraint
import jp.hotdrop.compl.model.OrmaDatabase

object OrmaHolder {

    private val DBNAME = "compl.orma.db"
    lateinit var ORMA: OrmaDatabase

    fun initialize(context: Context) {
        ORMA = OrmaDatabase.builder(context)
                .writeOnMainThread(AccessThreadConstraint.NONE)
                .name(DBNAME)
                .build()
    }
}