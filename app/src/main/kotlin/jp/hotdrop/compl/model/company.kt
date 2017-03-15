package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import jp.hotdrop.compl.dao.CategoryDao
import org.parceler.Parcel
import java.sql.Timestamp


@Parcel
@Table
class Company {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column(indexed = true)
    var categoryId: Int = 0

    @Column
    var name: String = ""

    @Column(indexed = true)
    var type: Type? = null

    @Column
    var employeesNum: Int = 0

    @Column
    var content: String? = null

    @Column
    var salaryLow: Int = 0

    @Column
    var salaryHigh: Int = 0

    @Column
    var jobCategory: String? = null

    @Column
    var url: String? = null

    @Column
    var note: String? = null

    @Column
    var evaluation: Int = 0

    @Column
    var registerDate: Timestamp? = null

    fun getGroup(): Category {
        return CategoryDao.find(categoryId)
    }
}
