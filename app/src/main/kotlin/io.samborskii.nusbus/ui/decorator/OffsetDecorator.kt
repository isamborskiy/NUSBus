package io.samborskii.nusbus.ui.decorator

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

open class OffsetDecorator : BaseDecorator {

    private val leftOffset: Int
    private val topOffset: Int
    private val rightOffset: Int
    private val bottomOffset: Int

    constructor(offset: Int, flags: Int) {
        this.leftOffset = if (flags and LEFT_OFFSET != 0) offset else 0
        this.topOffset = if (flags and TOP_OFFSET != 0) offset else 0
        this.rightOffset = if (flags and RIGHT_OFFSET != 0) offset else 0
        this.bottomOffset = if (flags and BOTTOM_OFFSET != 0) offset else 0
    }

    constructor(leftOffset: Int, topOffset: Int, rightOffset: Int, bottomOffset: Int) {
        this.leftOffset = leftOffset
        this.topOffset = topOffset
        this.rightOffset = rightOffset
        this.bottomOffset = bottomOffset
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (applyDecorator(view, parent)) {
            outRect.set(leftOffset, topOffset, rightOffset, bottomOffset)
        }
    }

    companion object {
        const val LEFT_OFFSET = 1 shl 1
        const val TOP_OFFSET = 1 shl 2
        const val RIGHT_OFFSET = 1 shl 3
        const val BOTTOM_OFFSET = 1 shl 4
    }
}
