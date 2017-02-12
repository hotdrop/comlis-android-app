package jp.hotdrop.compl.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Setter
import com.github.gfx.android.orma.annotation.Table

@Table
data class Company (
        @Setter("id") @PrimaryKey(autoincrement = true) var id: Long = 0,
        @Setter("category") @Column(indexed = true) var category: Category?,
        @Setter("name") @Column var name: String = "",
        @Setter("employeesNum") @Column var employeesNum: Int = 0,
        @Setter("content") @Column var content: String = "",
        @Setter("salary") @Column var salary: Int = 0,
        @Setter("jobCategory") @Column var jobCategory: String = "",
        @Setter("url") @Column var url: String = "",
        @Setter("note") @Column var note: String = "",
        @Setter("evaluation") @Column var evaluation: Int = 0
)

