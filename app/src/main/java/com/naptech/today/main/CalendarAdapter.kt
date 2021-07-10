package com.naptech.today.main

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.naptech.today.R
import kotlinx.android.synthetic.main.item_date.view.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class CalendarAdapter(val context: Context): RecyclerView.Adapter<CalendarAdapter.CalendarHolder>() {

    private val now = LocalDate.now()
    var selectDate: LocalDate = now; private set

    private var selectView: View? = null

    private var onSelectedListener: ((date: LocalDate, pos: Int) -> Unit)? = null
    fun setOnSelectedListener(listener: ((date: LocalDate, pos: Int) -> Unit)?) { onSelectedListener = listener }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CalendarHolder(LayoutInflater.from(context).inflate(
        R.layout.item_date, parent, false))
    override fun onBindViewHolder(holder: CalendarHolder, position: Int) = holder.bind(position)
    override fun getItemCount() = Int.MAX_VALUE

    inner class CalendarHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int) {
            val date = now.plusDays((position - (Int.MAX_VALUE / 2)).toLong())
            with(itemView) {
                if (selectView == null && date == now) {
                    selectView = itemView
                }
                if (date == selectDate) select(itemView)
                else deselect()
                setOnClickListener {
                    if (itemView != selectView) {
                        selectView?.let { deselect(it) }
                        select(itemView)
                        selectView = itemView
                        selectDate = date
                        onSelectedListener?.let { it(selectDate, position) }
                    }
                }
                text_date_month.text = if (now == date) "Today" else if (date.dayOfMonth == 1) date.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH) else null
                text_date_day.text = date.dayOfMonth.toString()
                text_date_week.text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            }
        }

        private fun select(view: View = itemView) {
            view.text_date_day.setTextColor(context.getColor(R.color.text_blue_dark))
            view.text_date_week.setTextColor(context.getColor(R.color.text_blue_dark))
            view.layout_date_root.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.layout_date_selected))
            view.layout_date_root.elevation = 32f
        }
        private fun deselect(view: View = itemView) {
            view.text_date_day.setTextColor(context.getColor(R.color.text_black_very_light))
            view.text_date_week.setTextColor(context.getColor(R.color.text_black_very_light))
            view.layout_date_root.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.layout_date_deselected))
            view.layout_date_root.elevation = 0f
        }
    }
}