package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import org.parceler.Parcel
import java.sql.Timestamp

@Parcel
@Table
class Company {

    @PrimaryKey(autoincrement = true)
    var id: Long = 0
    @Column var name: String = ""
    @Column(indexed = true) var category: Category? = null
    @Column(indexed = true) var type: Type? = null
    @Column var employeesNum: Int = 0
    @Column var content: String = ""
    @Column var salaryLow: Int = 0
    @Column var salaryHigh: Int = 0
    @Column var jobCategory: String = ""
    @Column var url: String = ""
    @Column var note: String = ""
    @Column var evaluation: Int = 0
    @Column var registerDate: Timestamp? = null
}

