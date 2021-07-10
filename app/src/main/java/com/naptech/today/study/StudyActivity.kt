package com.naptech.today.study

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.R
import com.naptech.today.data.SubjectData
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import com.naptech.today.func.firebase.FirebaseLogin
import com.naptech.today.main.MainActivity
import com.naptech.today.util.NaptechUtil
import com.naptech.today.util.SerializableDate
import com.naptech.today.util.SerializableDateTime
import com.naptech.today.util.SerializableTime
import kotlinx.android.synthetic.main.activity_study.*
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class StudyActivity : NaptechActivity() {

    private var job: Job? = null

    private val firebaseLogin = FirebaseLogin(this)
    private lateinit var startTime: LocalDateTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        firebaseLogin.getInstance()
        val reference = FirebaseDatabase.getInstance().reference.child("study").child(firebaseLogin.user!!.uid)
        val firebaseLogin = FirebaseDatabaseMap(reference).start()

        val subject = FirebaseDatabaseMap(reference.child("subject")).start()

        firebaseLogin.setOnUpdateListener(object: FirebaseDatabaseMap.OnUpdateListener {
            override fun onRemoved(key: String, removedData: Any?) {
                if (key == "status") finishStudy()
            }
        })

        val data = Json { allowStructuredMapKeys = true }.decodeFromString<Pair<SerializableDateTime, SubjectData>>(intent.getStringExtra("data")!!)

        startTime = data.first.localDateTime
        val subjectData = data.second

        text_study_subject.text = subjectData.name

        card_popup_complete.setOnClickListener {
            var key: String? = null
            val json = Json { allowStructuredMapKeys = true }.encodeToString(subjectData)
            subject.forEach { // TODO("개선 필요")
                if (it.value.toString() == json) {
                    key = it.key
                    return@forEach
                }
            }

            val logMap = subjectData.log.toMutableMap()
            val logList = logMap[SerializableDate(LocalDate.now())]?.toMutableList() ?: mutableListOf()
            logList.add(SubjectData.StudyLog(data.first, SerializableDateTime(LocalDateTime.now()), "")) // TODO("memo 설정할 것 and 날짜별로 시간 넣기")
            logMap[SerializableDate(LocalDate.now())] = logList
            key?.let { subject[it] = Json { allowStructuredMapKeys = true }.encodeToString(SubjectData(subjectData.name, subjectData.studyTime, subjectData.icon, subjectData.color, logMap.toMap())) }
            FirebaseDatabaseMap(reference).remove("status")
            finishStudy()
        }

        text_study_cancel.setOnClickListener {
            FirebaseDatabaseMap(reference).remove("status")
            finishStudy()
        }
    }

    override fun onResume() {
        super.onResume()
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                val time = Duration.between(startTime, LocalDateTime.now())
                val (h1, h2, m1, m2) = listOf(time.toHours().toInt() / 10, time.toHours().toInt() % 10, time.toMinutes().toInt() % 60 / 10, time.toMinutes().toInt() % 60 % 10)
                picker_study_hour1.smoothScrollToPosition(if (h1 == 0 && picker_study_hour1.value == 9) 10 else h1)
                picker_study_hour2.smoothScrollToPosition(if (h2 == 0 && picker_study_hour2.value == 9) 10 else h2)
                picker_study_minute1.smoothScrollToPosition(if (m1 == 0 && picker_study_minute1.value == 5) 10 else m1)
                picker_study_minute2.smoothScrollToPosition(if (m2 == 0 && picker_study_minute2.value == 9) 10 else m2)
                delay(1000)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }

    fun finishStudy() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}