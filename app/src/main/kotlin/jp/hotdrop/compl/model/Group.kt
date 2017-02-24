package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import org.parceler.Parcel

@Parcel
@Table
class Group {

    @PrimaryKey(autoincrement = true) var id: Int = 0
    @Column var name: String? = null
    @Column var point: Int = 0
    @Column var viewOrder: Int = 0

    fun change(group: Group) {
        this.id = group.id
        this.name = group.name
        this.point = group.point
        this.viewOrder = group.viewOrder
    }

    override fun equals(other: Any?): Boolean {
        return (other as Group).id == id || super.equals(other)
    }

    override fun hashCode(): Int {
        return id
    }
}