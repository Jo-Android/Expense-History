package com.hgtech.expensehistory.ui.manager

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.hgtech.expensehistory.R
import com.hgtech.expensehistory.ui.custom.dialog.AskDialog.delete
import org.koin.core.component.KoinComponent
import kotlin.math.roundToInt

class SwipeListener(
    private val context: Context,
    private val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
    private val onSwiped: (position: Int) -> Unit,
//    private val isSecret: Boolean,
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT
//            or
//            ItemTouchHelper.RIGHT
),
    KoinComponent {

//    var isSwipeEnabled = true

    private var textStart: Double = 0.0
    private var title: String = ""
    private var previousDx: Float = 0F
    private var recRect: Paint? = null
    private var itemView: View? = null
    private var imageBottom: Int = 0
    private var imageLeft: Double = 0.0
    private var imageTop: Int = 0
    private val defaultPaddingImage: Float = getDP(6, context)
    private val rootMargin: Float = getDP(0, context)
    private var itemHeight: Int = 0
    private val paintText = Paint()

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        return false
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        delete(context) { it ->
            if (it)
                onSwiped.invoke(position)
            else
                adapter.apply {
                    notifyItemRemoved(position)
                    notifyItemInserted(position)
                }
        }
        /*when (direction) {
            ItemTouchHelper.LEFT -> {

                onSwiped.invoke(position, true)
            }
            ItemTouchHelper.RIGHT -> {
                onSwiped.invoke(position, false)
            }
        }
        previousDx = 0F*/
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        itemView = viewHolder.itemView
        recRect = Paint()
        if (recRect != null && itemView != null) {

            /*if (previousDx >= 0 && dX > 0) {
                recRect!!.color = ContextCompat.getColor(context, R.color.archive)
            } else {
                recRect!!.color = ContextCompat.getColor(context, R.color.delete)
            }*/
            recRect!!.color = ContextCompat.getColor(context, R.color.start_color)
            previousDx = dX


            paintText.textSize = getDP(12, context)
            paintText.color = ContextCompat.getColor(context, android.R.color.white)
            c.drawRoundRect(
                itemView!!.left + rootMargin + defaultPaddingImage,
                itemView!!.top + rootMargin + defaultPaddingImage,
                itemView!!.right - rootMargin - defaultPaddingImage,
                itemView!!.bottom.toFloat() - rootMargin - defaultPaddingImage,
                getDP(9, context),
                getDP(9, context),
                recRect!!
            )


            /*val bitmap = if (previousDx >= 0 && dX > 0) {
                ContextCompat.getDrawable(
                    context, if (isSecret)
                        R.drawable.ic_baseline_unarchive_24
                    else
                        R.drawable.ic_baseline_archive_24
                )?.toBitmap()
            } else {
                ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_baseline_delete_24
                )?.toBitmap()
            }*/

            val bitmap = ContextCompat.getDrawable(
                context, R.drawable.delete
            )?.toBitmap()

            if (bitmap != null) {

                bitmap.width = getDP(21, context).roundToInt()
                bitmap.height = getDP(25, context).roundToInt()

                itemHeight = (itemView!!.bottom - itemView!!.top + rootMargin / 2).roundToInt()
                imageLeft = if (previousDx >= 0 && dX > 0)
                    (itemView!!.left + bitmap.width + getDP(20, context)).toDouble()
                else
                    (itemView!!.right - bitmap.width * 2 - getDP(20, context)).toDouble()

                imageTop =
                    itemView!!.top + itemHeight / 2 - bitmap.height / 2 - (defaultPaddingImage * 2).roundToInt()
                imageBottom = imageTop + bitmap.height

                c.drawBitmap(
                    bitmap, null, Rect(
                        imageLeft.roundToInt(),
                        imageTop,
                        (imageLeft + bitmap.width).roundToInt(),
                        imageBottom
                    ), Paint()
                )

                /*title = if (previousDx >= 0 && dX > 0) {
                    context.getString(
                        if (isSecret)
                            R.string.unarchive
                        else
                            R.string.archive
                    )
                } else
                    context.getString(R.string.delete)*/

                title = context.getString(R.string.delete)

                textStart =
                    imageLeft - bitmap.width / 2 - paintText.letterSpacing * title.length / 2 + defaultPaddingImage

                c.drawText(
                    title,
                    textStart.toFloat(),
                    (imageBottom + getDP(17, context)), // 12 DP Text Size + 5 Dp Margin
                    paintText
                )
            }
        }
    }
}