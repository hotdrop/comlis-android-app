package jp.hotdrop.compl.view.parts

import com.airbnb.lottie.LottieAnimationView

class FavoriteStars(private val animationView1: LottieAnimationView,
                    private val animationView2: LottieAnimationView,
                    private val animationView3: LottieAnimationView) {

    /**
     * 拡張関数
     */
    private fun LottieAnimationView.reset() {
        this.cancelAnimation()
        this.progress = 0.toFloat()
    }

    private fun LottieAnimationView.setFavoriteStar(): LottieAnimationView = this.apply {
        setAnimation("FavoriteStar.json", LottieAnimationView.CacheStrategy.Weak)
    }

    init {
        animationView1.setFavoriteStar()
        animationView2.setFavoriteStar()
        animationView3.setFavoriteStar()
    }

    fun playAnimationToOne() {
        animationView1.playAnimation()
        animationView2.reset()
        animationView3.reset()
    }

    fun playAnimationToTwo() {
        animationView1.playAnimation()
        animationView2.playAnimation()
        animationView3.reset()
    }


    fun playAnimationToThree() {
        animationView1.playAnimation()
        animationView2.playAnimation()
        animationView3.playAnimation()
    }

    fun clear() {
        animationView1.reset()
        animationView2.reset()
        animationView3.reset()

    }

    fun playAnimation(toFavoriteNum: Int) {
        clear()
        listOf(animationView1, animationView2, animationView3)
                .take(toFavoriteNum)
                .forEach { it.playAnimation() }
    }
}