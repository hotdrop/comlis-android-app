package jp.hotdrop.comlis.view.parts

import android.animation.FloatEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.text.SpannableString
import android.text.TextPaint
import android.text.format.DateUtils
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance
import android.util.Property
import android.view.animation.LinearInterpolator
import android.widget.TextView
import jp.hotdrop.comlis.R

class RainbowAnimationText constructor(
        context: Context,
        targetTextView: TextView
) {

    private val ANIM_COLOR_SPAN_PROPERTY =
            object : Property<AnimatedColorSpan, Float>(Float::class.java, "ANIM_COLOR_SPAN_PROPERTY") {
                override fun set(span: AnimatedColorSpan, value: Float?) { span.translateXPercent = value ?: return }
                override fun get(span: AnimatedColorSpan): Float? = span.translateXPercent
            }

    private val objectAnimator: ObjectAnimator

    init {
        val span = AnimatedColorSpan(context)
        val spannableStr = SpannableString(targetTextView.text.toString()).apply {
            setSpan(span, 0, targetTextView.text.toString().length, 0)
        }

        objectAnimator = ObjectAnimator.ofFloat(span, ANIM_COLOR_SPAN_PROPERTY, 0f, 100f)
                .apply {
                    setEvaluator(FloatEvaluator())
                    addUpdateListener{ targetTextView.text = spannableStr }
                    interpolator = LinearInterpolator()
                    duration = DateUtils.MINUTE_IN_MILLIS * 3
                    repeatCount = ValueAnimator.INFINITE
                }
    }

    fun animationStart() {
        objectAnimator.start()
    }

    class AnimatedColorSpan constructor(context: Context): CharacterStyle(), UpdateAppearance {

        private val colors: IntArray = context.resources.getIntArray(R.array.rainbow)
        private var shader: Shader? = null
        private val matrix = Matrix()
        var translateXPercent: Float = 0f

        override fun updateDrawState(tp: TextPaint?) {
            tp ?: return
            tp.style = Paint.Style.FILL
            val width = tp.textSize * colors.size

            matrix.reset()
            matrix.setRotate(90f)
            matrix.postTranslate(width * translateXPercent, 0f)

            if(shader == null) {
                shader = LinearGradient(0f, 0f, 0f, width, colors, null, Shader.TileMode.MIRROR)
            }
            shader?.setLocalMatrix(matrix)
            tp.shader = shader
        }
    }
}