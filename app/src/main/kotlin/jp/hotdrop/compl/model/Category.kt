package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table
import java.util.*

@Table
class Category {

    @PrimaryKey(autoincrement = true)
    var id: Int = 0

    @Column(indexed = true, unique = true)
    var name: String = ""

    /**
     * このColorTypeはColorDataUtilで定義した文字列を持っている・・
     * ColorResとして使う場合はColorDataUtilコンバートする
     */
    @Column
    var colorType: String = ""

    @Column
    var point: Int = 0

    @Column(indexed = true)
    var viewOrder: Int = 0

    @Column
    var registerDate: Date? = null

    @Column
    var updateDate: Date? = null
}