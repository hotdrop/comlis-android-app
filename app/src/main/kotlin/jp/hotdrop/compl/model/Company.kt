package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import java.util.*

@Table
class Company {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    // 更新が必要なのでColumnは全てindexed = trueにする
    @Column(indexed = true)
    var categoryId: Int = 0

    @Column(indexed = true)
    var name: String = ""

    @Column(indexed = true)
    var overview: String? = null

    @Column(indexed = true)
    var employeesNum: Int = 0

    @Column(indexed = true)
    var salaryLow: Int = 0

    @Column(indexed = true)
    var salaryHigh: Int = 0

    @Column(indexed = true)
    var wantedJob: String? = ""

    @Column(indexed = true)
    var url: String? = null

    @Column(indexed = true)
    var note: String? = null

    @Column(indexed = true)
    var viewOrder: Int = 0

    @Column(indexed = true)
    var favorite: Int = 0

    @Column
    var registerDate: Date? = null

    /**
     * 更新日はEditFragmentでの更新のみ反映する
     */
    @Column
    var updateDate: Date? = null
}
