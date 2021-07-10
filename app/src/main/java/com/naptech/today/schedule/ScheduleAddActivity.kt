package com.naptech.today.schedule

import android.os.Bundle
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.R
import com.naptech.today.func.NaptechActivity
import com.naptech.today.data.ScheduleData
import com.naptech.today.data.TagData
import com.naptech.today.enum.IconEnum
import com.naptech.today.enum.ScheduleStatus
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import com.naptech.today.func.firebase.FirebaseLogin
import com.naptech.today.util.NaptechUtil
import com.naptech.today.util.SerializableDateTime
import kotlinx.android.synthetic.main.activity_schedule_add.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class ScheduleAddActivity : NaptechActivity() {

    private val firebaseLogin = FirebaseLogin(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_add)

        firebaseLogin.getInstance()
        if (firebaseLogin.user == null) firebaseLogin.login(FirebaseLogin.LoginOption.ANONYMOUSLY)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val reference = databaseReference.child("schedule").child(firebaseLogin.user!!.uid)
        val database = FirebaseDatabaseMap(reference).start()

        val data = intent.getStringExtra("data")
        val scheduleData = data?.let { Json.decodeFromString<ScheduleData>(it) }


        val now = if (data == null) LocalDateTime.now() else scheduleData!!.time.localDateTime
        text_add_year.text = now.year.toString()
        picker_add_month.value = now.month.value
        picker_add_month.setOnValueChangedListener { _, oldVal, newVal ->
            picker_add_day.maxValue = if (newVal == 2) {
                val year = Integer.parseInt(text_add_year.text.toString())
                if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0 ) 29 else 28
            } else if (newVal in listOf(4, 6, 9, 11)) 30 else 31
            if (oldVal == 12 && newVal == 1) {
                text_add_year.text = (Integer.parseInt(text_add_year.text.toString()) + 1).toString()
            }
            else if (oldVal == 1 && newVal == 12) {
                text_add_year.text = (Integer.parseInt(text_add_year.text.toString()) - 1).toString()
            }
        }
        picker_add_day.value = now.dayOfMonth
        picker_add_hour.value = now.hour % 12
        picker_add_hour.setOnValueChangedListener { _, oldVal, newVal ->
            if (oldVal == 12 && newVal == 1) {
//                TODO("애니메이션")
                text_add_am_pm.text = if (text_add_am_pm.text == "AM") "PM" else "AM"
            }
            else if (oldVal == 1 && newVal == 12) {
//                TODO("애니메이션")
                text_add_am_pm.text = if (text_add_am_pm.text == "AM") "PM" else "AM"
            }
        }
        picker_add_minute.value = now.minute
        text_add_am_pm.text = if (now.hour <= 12) "AM" else "PM"

        scheduleData?.let {
            edit_add_label.setText(it.title)
            edit_add_memo.setText(it.text)
        }

        if (scheduleData != null) text_popup_edit3.setOnClickListener {
            var key: String? = null
            val json = Json.encodeToString(scheduleData)
            database.forEach { // TODO("개선 필요")
                if (it.value.toString() == json) {
                    key = it.key
                    return@forEach
                }
            }
            database.remove(key)
            finish()
        } else text_popup_edit3.visibility = View.GONE

        var colorPick = scheduleData?.tag?.color ?: getColor(R.color.accentedGray)
        val colorViewList = listOf(getColor(R.color.accentedRed) to view_red2, getColor(R.color.accentedOrange) to view_orange2, getColor(R.color.accentedYellow) to view_yellow2, getColor(R.color.accentedGreen) to view_green2, getColor(R.color.accentedBlue) to view_blue2, getColor(R.color.accentedViolet) to view_violet2, getColor(R.color.accentedGray) to view_gray2)
        colorViewList.forEach {
            it.second.setOnClickListener { _ ->
                colorViewList.find { l -> l.first == colorPick }?.second?.apply {
                    setBackgroundResource(R.drawable.rectangle_16)
                }
                it.second.setBackgroundResource(R.drawable.ic_complete)
                colorPick = it.first
            }
        }
        colorViewList.find { it.first == colorPick }?.second?.setBackgroundResource(R.drawable.ic_complete)

        layout_add_button.setOnClickListener {
            val dateTime = SerializableDateTime(Integer.parseInt(text_add_year.text.toString()), picker_add_month.value, picker_add_day.value, picker_add_hour.value + if (text_add_am_pm.text == "AM") 0 else 12, picker_add_minute.value)
            val newData = ScheduleData(dateTime, edit_add_label.text.toString(), edit_add_memo.text.toString(), TagData("", colorPick), scheduleData?.status ?: ScheduleStatus.None) // TODO("TAG 설정")
            val key = if (data == null) NaptechUtil.timestamp.toString() else {
                var key: String? = null
                val json = Json.encodeToString(scheduleData)
                database.forEach { // TODO("개선 필요")
                    if (it.value.toString() == json) {
                        key = it.key
                        return@forEach
                    }
                }
                key ?: NaptechUtil.timestamp.toString()
            }
            database[key] = Json.encodeToString(newData)
            finish()
        }
    }
}