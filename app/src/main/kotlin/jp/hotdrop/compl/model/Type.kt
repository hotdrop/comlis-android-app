package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import org.parceler.Parcel

@Parcel
@Table
class Type {
    @PrimaryKey(autoincrement = true) var id: Int = 0
    @Column var name: String? = null
    @Column var point: Int = 0

    override fun equals(other: Any?): Boolean {
        return (other as Type).id == id || super.equals(other)
    }

    override fun hashCode(): Int {
        return id
    }
}