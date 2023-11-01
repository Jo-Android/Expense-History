package com.hgtech.expensehistory.ui.manager

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionBinding
import com.hgtech.expensehistory.databinding.LayoutItemTitleDescriptionEditBinding
import com.hgtech.expensehistory.databinding.LayoutTitleDescriptionBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun LayoutItemTitleDescriptionBinding.getText(): String {
    return value.text.toString()
}

fun LayoutItemTitleDescriptionEditBinding.getText(): String {
    return value.text.toString()
}

fun LayoutTitleDescriptionBinding.getText(): String {
    return value.text.toString()
}

fun hideKeyboard(activity: FragmentActivity) {
    val inputMethodManager: InputMethodManager = activity.getSystemService(
        Activity.INPUT_METHOD_SERVICE
    ) as InputMethodManager
    if (inputMethodManager.isAcceptingText) {
        inputMethodManager.hideSoftInputFromWindow(
            (activity.currentFocus ?: return).windowToken,
            0
        )
    }
    activity.currentFocus?.clearFocus()
}

fun getDp(dp: Float, resources: Resources): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp, resources
            .displayMetrics
    ).toInt()
}

fun setupDatePicker(activity: FragmentActivity, date: AppCompatTextView) {
    hideKeyboard(activity)
    val c = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        activity,
        { _, year, monthOfYear, dayOfMonth ->
            if (year != 0) {
                with(date) {
                    visibility = View.VISIBLE
                    text = StringBuilder(dayOfMonth.toString())
                        .append("/")
                        .append(monthOfYear + 1)
                        .append("/")
                        .append(year)
                }
            }
        },
        c[Calendar.YEAR],
        c[Calendar.MONTH],
        c[Calendar.DAY_OF_MONTH]
    )
    datePickerDialog.show()
}

fun getDate(string: String): Date? {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return formatter.parse(string)
}

fun Date?.getItemDate(): String? {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    return this?.let { formatter.format(it) }
}

fun getDate(): Date {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.US)
    formatter.parse(formatter.format(System.currentTimeMillis())).also {
        return if (it == null) {
            Log.e("TAG", "Error Getting TOday Date")
            Date()
        } else
            it
    }
}

fun getMonth(date: Date?): Int? {
    Calendar.getInstance().also {
        return if (date != null) {
            it.time = date
            it.get(Calendar.MONTH)
        } else
            null
    }
}

fun getYear(date: Date?): Int? {
    Calendar.getInstance().also {
        return if (date != null) {
            it.time = date
            it.get(Calendar.YEAR)
        } else
            null
    }
}

fun Date?.getMonthName(): String {
    return getMonthName(getMonth(this))
}

fun getMonthName(int: Int?): String {
    return when (int) {
        0 -> "January"
        1 -> "February"
        2 -> "March"
        3 -> "April"
        4 -> "May"
        5 -> "June"
        6 -> "July"
        7 -> "August"
        8 -> "September"
        9 -> "October"
        10 -> "November"
        11 -> "December"
        else -> ""
    }
}

fun Double.format(): String {
    return DecimalFormat("#,###.##").format(this)
}

fun getDP(dp: Int, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources
            .displayMetrics
    )
}

fun CoroutineScope.runWithLifecycle(lifecycle: Lifecycle, call: suspend () -> Unit): Job {
    return launch {
        var isComplete = false
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            try {
                if (!isComplete)
                    call.invoke()
            } finally {
                isComplete = true
            }
        }
    }
}
