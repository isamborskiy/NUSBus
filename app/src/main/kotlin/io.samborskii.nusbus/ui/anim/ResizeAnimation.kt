package io.samborskii.nusbus.ui.anim

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

private const val DEFAULT_ANIMATION_DURATION: Long = 500L

class ResizeAnimation(
    private var view: View,
    private var startHeight: Int,
    private val targetHeight: Int,
    duration: Long = DEFAULT_ANIMATION_DURATION
) : Animation() {

    init {
        this.duration = duration
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val newHeight = (startHeight + targetHeight * interpolatedTime).toInt()
        view.layoutParams.height = newHeight
        view.requestLayout()
    }

    override fun willChangeBounds(): Boolean = true
}
