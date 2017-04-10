package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import java.util.*

@Table
class Company {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column(indexed = true)
    var categoryId: Int = 0

    @Column(indexed = true)
    var name: String = ""

    @Column
    var overview: String? = null

    @Column
    var workPlace: String? = null

    @Column
    var doingBusiness: String? = null

    @Column
    var wantBusiness: String? = null

    @Column
    var employeesNum: Int = 0

    @Column
    var salaryLow: Int = 0

    @Column
    var salaryHigh: Int = 0

    @Column
    var wantedJob: String? = ""

    @Column
    var url: String? = null

    @Column
    var note: String? = null

    @Column(indexed = true)
    var viewOrder: Int = 0

    @Column
    var favorite: Int = 0

    @Column
    var registerDate: Date? = null

    /**
     * 更新日の更新はEditFragmentで編集した場合のみ
     */
    @Column
    var updateDate: Date? = null
}
