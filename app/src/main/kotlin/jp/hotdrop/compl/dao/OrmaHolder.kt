package jp.hotdrop.compl.dao

import android.content.Context
import com.github.gfx.android.orma.AccessThreadConstraint
import jp.hotdrop.compl.model.OrmaDatabase



object OrmaHolder {

    private val DB_NAME = "compl.orma.db"
    lateinit var buildDB: OrmaDatabase

    fun initialize(context: Context) {
        buildDB = OrmaDatabase.builder(context)
                /*.migrationStep(3, object : ManualStepMigration.ChangeStep() {
                    override fun change(helper: ManualStepMigration.Helper) {
                        helper.renameColumn("Category", "order", "viewOrder")
                        helper.renameColumn("Company", "order", "viewOrder")
                    }
                })*/
                .writeOnMainThread(AccessThreadConstraint.NONE)
                .name(DB_NAME)
                .build()
    }
}