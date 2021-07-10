package com.naptech.today.main.schedule

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.naptech.today.R
import com.naptech.today.schedule.SchedulePopupActivity
import com.naptech.today.data.ScheduleData
import com.naptech.today.enum.ScheduleStatus
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import kotlinx.android.synthetic.main.item_schedule.view.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ScheduleAdapter(private val activity: NaptechActivity?, private val scheduleList: MutableList<ScheduleData>, private val scheduleDatabase: FirebaseDatabaseMap): RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = ScheduleHolder(LayoutInflater.from(activity).inflate(R.layout.item_schedule, parent, false))

    override fun onBindViewHolder(holder: ScheduleHolder, position: Int) = holder.bind(scheduleList[position])

    override fun getItemCount() = scheduleList.size

    inner class ScheduleHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(data: ScheduleData) {
            with(itemView) {
                text_schedule_title.text = data.title
//                TODO("지역 따라서 패턴 변경")
                text_schedule_time.text = data.time.localDateTime.format(DateTimeFormatter.ofPattern("MM/dd hh:mm a", Locale.ENGLISH))
                data.tag?.let { view_schedule_tag.backgroundTintList = ColorStateList.valueOf(it.color) }

                when (data.status) {
                    ScheduleStatus.NotCompleted -> {
                        text_schedule_status.visibility = View.GONE
                        image_schedule_status.visibility = View.VISIBLE
                        layout_schedule_status.elevation = 16f

                        image_schedule_status.setImageResource(R.drawable.ic_not_complete)
                        layout_schedule_status.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.layout_select))
                        image_schedule_status.imageTintList = ColorStateList.valueOf(context.getColor(R.color.accentedRed))

                        layout_item_schedule_root.backgroundTintList = null
                    }
                    ScheduleStatus.Completed -> {
                        text_schedule_status.visibility = View.GONE
                        image_schedule_status.visibility = View.VISIBLE
                        layout_schedule_status.elevation = 0f

                        image_schedule_status.setImageResource(R.drawable.ic_complete)
                        layout_schedule_status.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.layout_select))
                        image_schedule_status.imageTintList = ColorStateList.valueOf(context.getColor(R.color.accentedGreen))

                        layout_item_schedule_root.backgroundTintList = null
                    }
                    ScheduleStatus.InProgress -> {
                        text_schedule_status.visibility = View.VISIBLE
                        image_schedule_status.visibility = View.GONE
                        layout_schedule_status.elevation = 16f

                        layout_schedule_status.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.layout_select))
                        layout_item_schedule_root.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.layout_accented))
                        text_schedule_status.text = "In Progress" // TODO("strings.xml")
                    }
                    else -> {
                        text_schedule_status.text = "STATUS IS NONE!"
                        text_schedule_status.visibility = View.INVISIBLE
                        image_schedule_status.visibility = View.GONE
                        layout_schedule_status.elevation = 0f

                        layout_schedule_status.backgroundTintList = null
                        layout_item_schedule_root.backgroundTintList = null
                    }
                }

                layout_schedule_status.setOnClickListener {
                    var key: String? = null
                    val json = Json.encodeToString(data)
                    scheduleDatabase.forEach { // TODO("개선 필요")
                        if (it.value.toString() == json) {
                            key = it.key
                            return@forEach
                        }
                    }
                    key?.let { k ->
                        val d = ScheduleData(data.time, data.title, data.text, data.tag, when (data.status) { // TODO("개선 필요")
                            ScheduleStatus.NotCompleted -> ScheduleStatus.None
                            ScheduleStatus.Completed -> if (data.time.localDateTime.isBefore(LocalDateTime.now())) ScheduleStatus.NotCompleted else ScheduleStatus.None
                            ScheduleStatus.InProgress -> ScheduleStatus.Completed
                            else -> ScheduleStatus.InProgress
                        })
                        scheduleDatabase[k] = Json.encodeToString(d)
                    }
                }

                layout_item_schedule_root.setOnClickListener {
                    val intent = Intent(activity, SchedulePopupActivity::class.java)
                    intent.putExtra("data", Json.encodeToString(data))
                    activity?.startActivity(intent)
                }
            }
        }
    }
}