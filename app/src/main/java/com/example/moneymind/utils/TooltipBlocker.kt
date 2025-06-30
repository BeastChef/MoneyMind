package com.example.moneymind.utils

import android.os.Build
import android.view.View
import android.view.ViewGroup

object TooltipBlocker {

    fun disableAllTooltips(root: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            traverseAndClear(root)
        }
    }

    private fun traverseAndClear(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            view.tooltipText = null
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                traverseAndClear(view.getChildAt(i))
            }
        }
    }
}