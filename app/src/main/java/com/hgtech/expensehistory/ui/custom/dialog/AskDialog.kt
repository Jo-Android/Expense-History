package com.hgtech.expensehistory.ui.custom.dialog

import android.app.Dialog
import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.hgtech.expensehistory.R

object AskDialog {


    fun delete(
        context: Context,
        onSelection: (isConfirm: Boolean) -> Unit
    ) {
        setup(
            context,
            context.getString(R.string.delete_item),
            R.drawable.delete,
            context.getString(R.string.delete),
            context.getString(R.string.cancel),
            onSelection
        )
    }

    fun deleteRoot(
        context: Context,
        onSelection: (isConfirm: Boolean) -> Unit
    ) {
        setup(
            context,
            context.getString(R.string.delete_root),
            R.drawable.delete,
            context.getString(R.string.delete),
            context.getString(R.string.cancel),
            onSelection
        )
    }

    fun setup(
        context: Context,
        message: String,
        baseImage: Int,
        yesButton: String,
        cancelButton: String,
        onSelection: (isConfirm: Boolean) -> Unit
    ): Dialog {
        val dialog = createDialog(context, R.layout.dialog_ask)
        dialog.findViewById<AppCompatTextView>(R.id.confirmBtn).text = yesButton
        dialog.findViewById<AppCompatTextView>(R.id.cancelConfirm).text = cancelButton
        dialog.findViewById<AppCompatTextView>(R.id.textView6).text = message
        dialog.findViewById<AppCompatImageView>(R.id.confirmImage).setImageResource(baseImage)
        var isDismissed = false
        dialog.findViewById<AppCompatTextView>(R.id.cancelConfirm).setOnClickListener {
            dialog.dismiss()
            isDismissed = true
            onSelection.invoke(false)
        }
        dialog.findViewById<AppCompatTextView>(R.id.confirmBtn).setOnClickListener {
            dialog.dismiss()
            isDismissed = true
            onSelection.invoke(true)
        }
        dialog.findViewById<AppCompatImageView>(R.id.closeDialog).setOnClickListener {
            dialog.dismiss()
            isDismissed = true
            onSelection.invoke(false)
        }


        dialog.setOnDismissListener {
//            Log.e("Dialog State", "dissmiss")
            if (!isDismissed)
                onSelection.invoke(false)
        }
        return dialog
    }

    fun showEditDialog(text: String, context: Context, onClicked: () -> Unit) {
        createBottomDialog(context, R.layout.layout_confirm_dialog).also { dialog ->
            dialog.findViewById<AppCompatTextView>(R.id.title).text = text
            dialog.findViewById<AppCompatTextView>(R.id.confirm).setOnClickListener {
                dialog.dismiss()
                onClicked.invoke()
            }
            dialog.findViewById<AppCompatTextView>(R.id.cancel).setOnClickListener {
                dialog.dismiss()
            }
        }
    }
}