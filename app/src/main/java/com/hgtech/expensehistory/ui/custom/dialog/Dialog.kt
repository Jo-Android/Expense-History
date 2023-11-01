package com.hgtech.expensehistory.ui.custom.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.widget.LinearLayout
import com.hgtech.expensehistory.R

fun createDialog(context: Context, layoutId: Int): Dialog {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.window!!.decorView.setBackgroundResource(R.drawable.background_rounded_corner)
    dialog.window!!.decorView.setPadding(0, 0, 0, 0)
    if (context is Activity) {
        dialog.window!!.attributes.height =
            LinearLayout.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes.width =
            ((getWidth(context) - getDP(40, context)).toInt())
    }
    dialog.setContentView(layoutId)
    dialog.show()
    return dialog
}


@Suppress("DEPRECATION")
fun getWidth(activity: Activity): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = activity.windowManager.currentWindowMetrics
        val insets: Insets = windowMetrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
}


fun getDP(dip: Int, context: Context): Float {
    return (TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip.toFloat(),
        context.resources.displayMetrics
    ))
}

fun createBottomDialog(context: Context, layoutId: Int): Dialog {
    val dialog = Dialog(context)
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialog.setContentView(layoutId)
    dialog.window?.setLayout(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialog.window?.setGravity(Gravity.BOTTOM)

    dialog.show()
    return dialog
}

