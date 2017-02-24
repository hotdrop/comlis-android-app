package jp.hotdrop.compl.model

import android.support.annotation.Nullable
import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import org.parceler.Parcel
import java.sql.Timestamp

@Parcel
@Table
class Company {

    @PrimaryKey(autoincrement = true) var id: Int = 0
    @Column var name: String = ""
    @Column(indexed = true) var group: Group? = null
    @Column(indexed = true) var type: Type? = null

    @Column var employeesNum: Int = 0
    @Column var content: String? = null
    @Column var salaryLow: Int = 0
    @Column var salaryHigh: Int = 0
    @Column var jobCategory: String? = null
    @Column var url: String? = null
    @Column var note: String? = null
    @Column var evaluation: Int = 0
    @Column var registerDate: Timestamp? = null

    override fun equals(other: Any?): Boolean {
        return (other as Company).id == id || super.equals(other)
    }

    override fun hashCode(): Int {
        return id
    }
}

