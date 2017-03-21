package jp.hotdrop.compl.dao

import android.content.Context
import com.github.gfx.android.orma.AccessThreadConstraint
import jp.hotdrop.compl.model.OrmaDatabase



object OrmaHolder {

    private val DB_NAME = "compl.orma.db"
    lateinit var ORMA: OrmaDatabase

    fun initialize(context: Context) {
        ORMA = OrmaDatabase.builder(context)
                /*.migrationStep(2, object : ManualStepMigration.ChangeStep() {
                    override fun change(helper: ManualStepMigration.Helper) {
                    }
                })*/
                .writeOnMainThread(AccessThreadConstraint.NONE)
                .name(DB_NAME)
                .build()
    }
}