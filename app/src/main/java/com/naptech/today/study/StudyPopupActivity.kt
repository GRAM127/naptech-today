package com.naptech.today.study

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.R
import com.naptech.today.data.SubjectData
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import com.naptech.today.func.firebase.FirebaseLogin
import com.naptech.today.util.SerializableDate
import com.naptech.today.util.SerializableDateTime
import com.naptech.today.util.SerializableTime
import kotlinx.android.synthetic.main.activity_study_popup.*
import kotlinx.android.synthetic.main.item_study2.view.image_study_icon
import kotlinx.android.synthetic.main.item_study2.view.text_study_name
import kotlinx.android.synthetic.main.item_study2.view.text_study_time
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class StudyPopupActivity : NaptechActivity() {

    private val firebaseLogin = FirebaseLogin(this)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_popup)
        firebaseLogin.getInstance()

        val data = intent.getStringExtra("data")
        val subjectData = Json { allowStructuredMapKeys = true }.decodeFromString<SubjectData>(data!!)

        var studyTime = SerializableTime(0, 0, 0).localTime
        subjectData.log[SerializableDate(LocalDate.now())]?.let { list ->
            list.forEach { studyTime = studyTime.plus(Duration.between(it.start.localDateTime, it.end.localDateTime)) }
        }
        val isOver = studyTime.isAfter(subjectData.studyTime.localTime)

        with (layout_subject_popup) {
            image_study_icon.setImageResource(subjectData.icon.resourceId)
//            image_study_icon.imageTintList = ColorStateList.valueOf(subjectData.color)
            text_study_name.text = subjectData.name
            text_study_time.text = if (subjectData.studyTime.hour != 0) "하루 ${subjectData.studyTime.hour}시간 " else "" + if (subjectData.studyTime.minute != 0) "${subjectData.studyTime.minute}분 " else "" + "도전!"
        }

        progress_study_time.max = if (isOver) subjectData.studyTime.localTime.toSecondOfDay() / 60 else subjectData.studyTime.localTime.toSecondOfDay() / 60
        progress_study_time.progress = studyTime.toSecondOfDay() / 60

        val (h, m) = Duration.between(studyTime, subjectData.studyTime.localTime).let { listOf(it.toHours().toInt(), it.toMinutes().toInt() % 60) }
        text_study_progress.text = "${ if (studyTime.hour != 0) "${studyTime.hour}시간 " else "" }${ if (studyTime.minute != 0) "${studyTime.minute}분" else "" }  /  " + if (subjectData.studyTime.hour != 0) "${subjectData.studyTime.hour}시간 " else "" + if (subjectData.studyTime.minute != 0) "${subjectData.studyTime.minute}분" else "" + ""
        text_study_progress_message.text = if (studyTime.isBefore(subjectData.studyTime.localTime)) "목표까지 ${ if (h != 0) "${h}시간 " else "" }${ if (m != 0) "${m}분 " else "" }남았습니다. 파이팅!" else "목표를 달성한 당신, 대단해요!"


        text_popup_edit2.setOnClickListener {
            val intent = Intent(this, StudyAddActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
            finish()
        }

        card_subject_button.setOnClickListener {
            firebaseLogin.user?.let {
                val reference = FirebaseDatabase.getInstance().reference.child("study").child(it.uid)
                FirebaseDatabaseMap(reference)["status"] = Json { allowStructuredMapKeys = true }.encodeToString(Pair(SerializableDateTime(LocalDateTime.now()), subjectData))
                finish()
            }
        }


//        Chart
        val studyTimeList = mutableListOf<BarEntry>()
        for (i in 0..9) {
            val v = subjectData.log[SerializableDate(LocalDate.now().plusDays(-i.toLong()))]
            val floatArray = FloatArray(2)
            if (v != null) {
                var dt = 0f
                val st = Duration.between(LocalTime.MIN, subjectData.studyTime.localTime).toMinutes().toFloat()
                v.forEach { dt += Duration.between(it.start.localDateTime, it.end.localDateTime).toMinutes().toFloat() }
                if (dt < st) { floatArray[0] = dt; floatArray[1] = 0f }
                else { floatArray[0] = st; floatArray[1] = (dt - st) }
            } else {
                floatArray[0] = 0f; floatArray[1] = 0f
            }
            studyTimeList.add(BarEntry(10 - i.toFloat(), floatArray))
        }

        val barDataSet = BarDataSet(studyTimeList, "Hours Studied")
        barDataSet.colors = mutableListOf(getColor(R.color.accentedBlue), getColor(R.color.lightBlue))

        chart_account_hours_studied.apply {
            setScaleEnabled(true)
            setPinchZoom(false)
            animateXY(0, 800)
            description = null
            legend.isEnabled = false
            axisLeft.axisMinimum = 0f
            axisRight.axisMinimum = 0f
            axisRight.setDrawLabels(false)
            xAxis.setDrawGridLines(false)

            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.labelCount = 10
            xAxis.valueFormatter = object: ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val d = LocalDate.now().plusDays(value.toLong() - 10)
                    return "${d.dayOfMonth}일"
                }
            }
        }
        chart_account_hours_studied.data = BarData(barDataSet)
    }
}