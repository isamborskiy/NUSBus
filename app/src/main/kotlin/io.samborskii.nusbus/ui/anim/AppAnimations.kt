package io.samborskii.nusbus.ui.anim

import android.animation.Animator
import android.animation.ObjectAnimator
import android.util.Property
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator

fun heightToAnimator(view: View, height: Int, animDuration: Long): Animator {
    val curHeight = view.height
    return ObjectAnimator.ofInt(view, HeightProperty(), curHeight, height).apply {
        duration = animDuration
        interpolator = AccelerateDecelerateInterpolator()
    }
}

fun topMarginToAnimator(view: View, topMargin: Int, animDuration: Long): Animator {
    val curTopMargin = (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin
    return ObjectAnimator.ofInt(view, TopMarginProperty(), curTopMargin, topMargin).apply {
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

private class TopMarginProperty : Property<View, Int>(Int::class.java, "topMargin") {

    override operator fun get(view: View): Int? = view.top

    override operator fun set(view: View, value: Int) {
        (view.layoutParams as ViewGroup.MarginLayoutParams).topMargin = value
        view.layoutParams = view.layoutParams
    }
}


interface AnimationStartEndListener : Animator.AnimatorListener {
    override fun onAnimationRepeat(animation: Animator?) {
    }

    override fun onAnimationCancel(animation: Animator?) {
    }
}

interface AnimationEndListener : AnimationStartEndListener {
    override fun onAnimationStart(animation: Animator?) {
    }
}
