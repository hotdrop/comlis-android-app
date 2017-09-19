package jp.hotdrop.comlis.model

import com.github.gfx.android.orma.annotation.Column
import com.github.gfx.android.orma.annotation.PrimaryKey
import com.github.gfx.android.orma.annotation.Table

@Table
class JobEvaluation {

    @PrimaryKey(auto = false, autoincrement = false)
    var companyId: Int = 0

    @Column
    var correctSentence: Boolean = false

    @Column
    var developmentEnv: Boolean = false

    @Column
    var wantSkill: Boolean = false

    @Column
    var personImage: Boolean = false

    @Column
    var appeal: Boolean = false

    @Column
    var jobOfferReason: Boolean = false
}