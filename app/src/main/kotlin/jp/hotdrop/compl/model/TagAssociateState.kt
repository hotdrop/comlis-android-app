package jp.hotdrop.compl.model

enum class TagAssociateState {

    ASSOCIATED {
        override fun flip() = RELEASE
    },
    RELEASE {
        override fun flip() = ASSOCIATED
    };

    abstract fun flip(): TagAssociateState
}