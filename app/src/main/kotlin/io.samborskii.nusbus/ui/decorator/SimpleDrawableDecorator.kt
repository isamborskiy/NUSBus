package io.samborskii.nusbus.ui.decorator

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView

open class SimpleDrawableDecorator(private val drawable: Drawable, private val border: Border) : BaseDecorator() {

    enum class Border {
        TOP,
        BOTTOM
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (applyDecorator(child, parent)) {
                val tx = child.translationX.toInt()
                val ty = child.translationY.toInt()

                val left = child.left + tx
                val right = child.right + tx
                val top: Int
                val bottom: Int
                if (border == Border.TOP) {
                    top = child.top + ty - drawable.minimumHeight
                    bottom = child.top + ty
                } else {
                    top = child.bottom + ty
                    bottom = child.bottom + ty + drawable.minimumHeight
                }

                drawable.setBounds(left, top, right, bottom)
                drawable.draw(c)
            }
        }
    }
}
