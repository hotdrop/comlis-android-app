package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import org.parceler.Parcel

@Parcel
@Table
class Category {

    @PrimaryKey(autoincrement = true) var id: Int = 0
    @Column var name: String? = null
    @Column var point: Int = 0
    @Column var viewOrder: Int = 0

    fun change(category: Category) {
        this.id = category.id
        this.name = category.name
        this.point = category.point
        this.viewOrder = category.viewOrder
    }

    override fun equals(other: Any?): Boolean {
        return (other as Category).id == id || super.equals(other)
    }

    override fun hashCode(): Int {
        return id
    }
}