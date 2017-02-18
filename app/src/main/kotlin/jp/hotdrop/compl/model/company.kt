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

    @PrimaryKey(autoincrement = true) var id: Long = 0
    @Column var name: String = ""
    @Column(indexed = true) @Nullable var category: Category? = null
    @Column(indexed = true) @Nullable var type: Type? = null

    @Column var employeesNum: Int = 0
    @Column @Nullable var content: String? = null
    @Column var salaryLow: Int = 0
    @Column var salaryHigh: Int = 0
    @Column @Nullable var jobCategory: String? = null
    @Column @Nullable var url: String? = null
    @Column @Nullable var note: String? = null
    @Column var evaluation: Int = 0
    @Column @Nullable var registerDate: Timestamp? = null
}

