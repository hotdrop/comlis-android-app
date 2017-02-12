package jp.hotdrop.compl.dao

import android.content.Context
import jp.hotdrop.compl.model.OrmaDatabase

object OrmaHolder {

    private val DBNAME = "compl.db"
    lateinit var ORMA: OrmaDatabase

    fun initialize(context: Context) {
        ORMA = OrmaDatabase.builder(context).name(DBNAME).build()
    }
}