package com.naptech.today.main.study

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.data.SubjectData
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.LocalDate


class StudyViewModel: ViewModel() {

    private val subjectList = mutableListOf<SubjectData>()
    private var studyAdapter: StudyAdapter? = null
    private var oldUid: String? = null

    private var database: FirebaseDatabaseMap? = null

    private fun setDatabase(uid: String) {
        database?.stop()
        subjectList.clear()
        val reference = FirebaseDatabase.getInstance().reference.child("study").child(uid).child("subject")
        database = FirebaseDatabaseMap(reference).start()
        database?.setOnUpdateListener(object: FirebaseDatabaseMap.OnUpdateListener {
            override fun onAdded(key: String, newData: Any?) {
                val data = Json { allowStructuredMapKeys = true }.decodeFromString<SubjectData>(newData.toString())
                subjectList.add(data)
                studyAdapter?.notifyItemInserted(subjectList.indexOf(data))
            }

            override fun onUpdated(key: String, oldData: Any?, newData: Any?) {
                val new = Json { allowStructuredMapKeys = true }.decodeFromString<SubjectData>(newData.toString())
                val index = subjectList.indexOf(Json { allowStructuredMapKeys = true }.decodeFromString(oldData.toString()))
                subjectList[index] = new
                studyAdapter?.notifyItemChanged(index)
            }

            override fun onRemoved(key: String, removedData: Any?) {
                val data = Json { allowStructuredMapKeys = true }.decodeFromString<SubjectData>(removedData.toString())
                studyAdapter?.notifyItemRemoved(subjectList.indexOf(data))
                subjectList.remove(data)
            }
        })
    }

    fun getStudyAdapter(activity: NaptechActivity, uid: String, date: LocalDate): StudyAdapter {
        if (oldUid != uid) {
            oldUid = uid; setDatabase(uid)
        }
        if (studyAdapter == null) {
            studyAdapter = StudyAdapter(activity, subjectList, date)
        }
        else if (studyAdapter!!.date != date) studyAdapter = StudyAdapter(activity, subjectList, date)
        return studyAdapter!!
    }

    fun getSubjectList(): List<SubjectData> = subjectList
}