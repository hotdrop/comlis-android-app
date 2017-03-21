package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import jp.hotdrop.compl.dao.CategoryDao
import java.sql.Timestamp


@Table
class Company {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column(indexed = true)
    var categoryId: Int = 0

    @Column
    var name: String = ""

    @Column
    var overview: String? = null

    @Column
    var employeesNum: Int = 0

    @Column
    var salaryLow: Int = 0

    @Column
    var salaryHigh: Int = 0

    @Column
    var url: String? = null

    @Column
    var note: String? = null

    @Column
    var order: Int = 0

    @Column(indexed = true)
    var favorite: Boolean = false

    @Column
    var registerDate: Timestamp? = null

    @Column
    var updateDate: Timestamp? = null

    fun getGroup(): Category {
        return CategoryDao.find(categoryId)
    }
}
