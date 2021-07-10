package com.naptech.today.main.study

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.naptech.today.R
import com.naptech.today.data.SubjectData
import com.naptech.today.func.NaptechActivity
import com.naptech.today.study.StudyPopupActivity
import com.naptech.today.util.SerializableDate
import com.naptech.today.util.SerializableTime
import kotlinx.android.synthetic.main.item_study.view.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

class StudyAdapter(private val activity: NaptechActivity, private val subjectList: MutableList<SubjectData>, val date: LocalDate): RecyclerView.Adapter<StudyAdapter.StudyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = StudyHolder(LayoutInflater.from(activity).inflate(R.layout.item_study, parent, false))
    override fun onBindViewHolder(holder: StudyHolder, position: Int) = holder.bind(subjectList[position])
    override fun getItemCount() = subjectList.size

    inner class StudyHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun bind(data: SubjectData) {
            var studyTime = SerializableTime(0, 0, 0).localTime
            data.log[SerializableDate(date)]?.let { list ->
                list.forEach { studyTime = studyTime.plus(Duration.between(it.start.localDateTime, it.end.localDateTime)) }
            }
            val isOver = studyTime.isAfter(data.studyTime.localTime)

            with(itemView) {
                image_study_icon.setImageResource(if (isOver) R.drawable.ic_complete else data.icon.resourceId)
                image_study_icon.imageTintList = if (isOver) ColorStateList.valueOf(activity.getColor(R.color.lightGreen)) else null
                text_study_name.text = data.name
                text_study_time.text = "하루 " + if (data.studyTime.hour != 0) { "${data.studyTime.hour}시간 " } else { "" } + if (data.studyTime.minute != 0) { "${data.studyTime.minute}분 " } else { "" + "도전!" }

                // TODO("풍선 라이브러리?")
                progress_study_time.max = if (isOver) data.studyTime.localTime.toSecondOfDay() / 60 else data.studyTime.localTime.toSecondOfDay() / 60
                progress_study_time.progress = studyTime.toSecondOfDay() / 60

                layout_item_subject_root.setOnClickListener {
                    val intent = Intent(activity, StudyPopupActivity::class.java)
                    intent.putExtra("data", Json { allowStructuredMapKeys = true }.encodeToString(data))
                    activity.startActivity(intent)
                }
            }
        }
    }
}