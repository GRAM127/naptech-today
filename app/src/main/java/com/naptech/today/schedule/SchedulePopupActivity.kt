package com.naptech.today.schedule

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.R
import com.naptech.today.data.ScheduleData
import com.naptech.today.enum.ScheduleStatus
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import com.naptech.today.func.firebase.FirebaseLogin
import kotlinx.android.synthetic.main.activity_schedule_popup.*
import kotlinx.android.synthetic.main.item_schedule_2.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SchedulePopupActivity : NaptechActivity() {

    private val firebaseLogin = FirebaseLogin(this)

    private var status = ScheduleStatus.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_popup)

        firebaseLogin.getInstance()
        if (firebaseLogin.user == null) firebaseLogin.login(FirebaseLogin.LoginOption.ANONYMOUSLY)
        val databaseReference = FirebaseDatabase.getInstance().reference
        val reference = databaseReference.child("schedule").child(firebaseLogin.user!!.uid)
        val database = FirebaseDatabaseMap(reference).start()

        val data = Json.decodeFromString<ScheduleData>(intent.getStringExtra("data").toString())

        schedule_popup.apply {
            text_schedule_title.text = data.title
            text_schedule_time.text = data.time.localDateTime.format(DateTimeFormatter.ofPattern("MM/dd hh:mm a", Locale.ENGLISH))
            view_schedule_tag.backgroundTintList = ColorStateList.valueOf(data.tag?.color ?: getColor(
                R.color.lightGray
            ))
            text_schedule_text.text = data.text
        }
        status = data.status
        setButton(data.status)

        val statusList = listOf(ScheduleStatus.InProgress, ScheduleStatus.Completed, ScheduleStatus.NotCompleted)
        listOf(card_popup_in_progress, card_popup_complete, card_popup_not_completed).forEachIndexed { i, view ->
            if (i == 2 && data.time.localDateTime.isAfter(LocalDateTime.now())) view.visibility = View.GONE
            else {
                view.setOnClickListener {
                    var key: String? = null
                    val json = Json.encodeToString(data)
                    database.forEach { // TODO("개선 필요")
                        if (it.value.toString() == json) {
                            key = it.key
                            return@forEach
                        }
                    }
                    val newStatus = if (view.cardElevation > 0) ScheduleStatus.None else statusList[i]
                    setButton(newStatus)
                    key?.let { k -> database[k] = Json.encodeToString(ScheduleData(data.time, data.title, data.text, data.tag, newStatus)) }
                }
            }
        }

        text_popup_edit.setOnClickListener {
            val intent = Intent(this, ScheduleAddActivity::class.java)
            intent.putExtra("data", Json.encodeToString(data))
            startActivity(intent)
            finish()
        }
    }

    private fun setButton(status: ScheduleStatus) {
        when (status) {
            ScheduleStatus.InProgress -> {
                card_popup_in_progress.cardElevation = 32f
                card_popup_complete.cardElevation = 0f
                card_popup_not_completed.cardElevation = 0f
            }
            ScheduleStatus.Completed -> {
                card_popup_in_progress.cardElevation = 0f
                card_popup_complete.cardElevation = 32f
                card_popup_not_completed.cardElevation = 0f
            }
            ScheduleStatus.NotCompleted -> {
                card_popup_in_progress.cardElevation = 0f
                card_popup_complete.cardElevation = 0f
                card_popup_not_completed.cardElevation = 32f
            }
            else -> {
                card_popup_in_progress.cardElevation = 0f
                card_popup_complete.cardElevation = 0f
                card_popup_not_completed.cardElevation = 0f
            }
        }
    }
}