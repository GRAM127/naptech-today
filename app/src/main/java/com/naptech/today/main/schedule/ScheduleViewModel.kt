package com.naptech.today.main.schedule

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import com.naptech.today.data.ScheduleData
import com.naptech.today.func.NaptechActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDateTime

class ScheduleViewModel: ViewModel() {

    private val scheduleList = mutableListOf<ScheduleData>()
    private var scheduleAdapter: ScheduleAdapter? = null

    private var scheduleDatabase: FirebaseDatabaseMap? = null
    private var oldUid: String? = null

    private fun setDatabase(uid: String, onUpdateListener: FirebaseDatabaseMap.OnUpdateListener) {
        scheduleDatabase?.stop()
        scheduleList.clear()
        val reference = FirebaseDatabase.getInstance().reference.child("schedule").child(uid)
        scheduleDatabase = FirebaseDatabaseMap(reference).start()
        scheduleDatabase?.setOnUpdateListener(object: FirebaseDatabaseMap.OnUpdateListener {
            override fun onAdded(key: String, newData: Any?) {
                val data = Json.decodeFromString<ScheduleData>(newData.toString())
                scheduleList.add(data)
                scheduleList.sortBy { d -> d.time.timestamp }
                scheduleAdapter?.notifyItemInserted(scheduleList.indexOf(data))
                onUpdateListener.onAdded(key, newData)
            }
            override fun onUpdated(key: String, oldData: Any?, newData: Any?) {
                val old = Json.decodeFromString<ScheduleData>(oldData.toString())
                val new = Json.decodeFromString<ScheduleData>(newData.toString())
                scheduleAdapter?.notifyItemRemoved(scheduleList.indexOf(old))
                scheduleList.remove(old)
                scheduleList.add(new)
                scheduleList.sortBy { d -> d.time.timestamp }
                scheduleAdapter?.notifyItemInserted(scheduleList.indexOf(new))
                onUpdateListener.onUpdated(key, oldData, newData)
            }
            override fun onRemoved(key: String, removedData: Any?) {
                val data = Json.decodeFromString<ScheduleData>(removedData.toString())
                scheduleAdapter?.notifyItemRemoved(scheduleList.indexOf(data))
                scheduleList.remove(data)
                onUpdateListener.onRemoved(key, removedData)
            }
        })
    }

    fun getScheduleAdapter(activity: NaptechActivity, uid: String, onUpdateListener: FirebaseDatabaseMap.OnUpdateListener): ScheduleAdapter {
        if (oldUid != uid) {
            oldUid = uid; setDatabase(uid, onUpdateListener)
        }
        if (scheduleAdapter == null) {
            scheduleAdapter = ScheduleAdapter(activity, scheduleList, scheduleDatabase!!)
        }
        return scheduleAdapter!!
    }

    fun findIndex(dateTime: LocalDateTime): Int {
        if (scheduleList.size == 0) return -1
        val index = scheduleList.indexOfFirst { it.time.localDateTime.isAfter(dateTime) }
        if (index < 0) return if (scheduleList.last().time.localDateTime.isBefore(dateTime)) run { scheduleList.size - 1 } else 0
        return index
    }
}