package io.samborskii.nusbus.ui.decorator

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class BaseDecorator : RecyclerView.ItemDecoration() {
    protected fun applyDecorator(view: View, parent: RecyclerView): Boolean = true
}
