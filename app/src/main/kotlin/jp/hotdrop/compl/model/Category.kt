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

    // TODO 今はViewに直接bindingしているためIntではなくStringにしている
    // TODO これはダメなのでさっさとViewModelにする
    @Column
    var point: String = ""

    @Column(indexed = true)
    var viewOrder: Int = 0

    fun change(o: Category) {
        this.id = o.id
        this.name = o.name
        this.point = o.point
        this.viewOrder = o.viewOrder
    }

    override fun equals(other: Any?): Boolean {
        return (other as Category).id == id || super.equals(other)
    }

    override fun hashCode(): Int {
        return id
    }
}