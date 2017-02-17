package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import org.parceler.Parcel

@Parcel
@Table
class Category {
    @PrimaryKey(autoincrement = true) var id: Long = 0
    @Column var name: String? = null
    @Column var point: Int = 0
}