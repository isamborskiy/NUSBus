package io.samborskii.nusbus.ui.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.util.Property
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun heightToAnimator(view: View, height: Int, animDuration: Long): Animator {
    val curHeight = view.height
    return ObjectAnimator.ofInt(view, HeightProperty(), curHeight, height).apply {
        duration = animDuration
        interpolator = AccelerateDecelerateInterpolator()
    }
}

private class HeightProperty : Property<View, Int>(Int::class.java, "height") {

    override operator fun get(view: View): Int? = view.height

    override operator fun set(view: View, value: Int) {
        view.layoutParams.height = value
        view.layoutParams = view.layoutParams
    }
}

abstract class EndActionListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }

    override fun onAnimationStart(animation: Animator?) {
    }
}
