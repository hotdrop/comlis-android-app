package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import org.parceler.Parcel

@Parcel
@Table
class Category {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column(indexed = true, unique = true)
    var name: String = ""

    @Column
    var point: Int = 0

    @Column(indexed = true)
    var viewOrder: Int = 0
}