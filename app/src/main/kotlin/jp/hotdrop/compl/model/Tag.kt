package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import java.util.*

@Table
class Tag {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column(indexed = true, unique = true)
    var name: String = ""

    /**
     * このColorTypeもCategory同様、ColorDataUtilでコンバートする
     */
    @Column
    var colorType: String = ""

    @Column(indexed = true)
    var viewOrder: Int = 0

    @Column
    var registerDate: Date? = null

    @Column
    var updateDate: Date? = null

}