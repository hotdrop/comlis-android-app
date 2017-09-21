package jp.hotdrop.comlis.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table

@Table
class RemoteKey {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column
    var dateEpoch: Long = 0
}