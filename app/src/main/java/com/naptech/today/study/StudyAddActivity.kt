package com.naptech.today.study

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.R
import com.naptech.today.data.SubjectData
import com.naptech.today.enum.IconEnum
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import com.naptech.today.func.firebase.FirebaseLogin
import com.naptech.today.util.NaptechUtil
import com.naptech.today.util.SerializableTime
import kotlinx.android.synthetic.main.activity_study_add.*
import kotlinx.android.synthetic.main.bottom_select.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StudyAddActivity : NaptechActivity() {
    private val firebaseLogin = FirebaseLogin(this)

    private var image = IconEnum.Subject0
    set(value) {
        imageView7.setImageResource(value.resourceId)
        field = value
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_add)
        firebaseLogin.getInstance()

        val reference = FirebaseDatabase.getInstance().reference.child("study").child(firebaseLogin.user!!.uid).child("subject")
        val database = FirebaseDatabaseMap(reference).start()

        val data = intent.getStringExtra("data")
        val subjectData = data?.let { Json { allowStructuredMapKeys = true }.decodeFromString<SubjectData>(it) }

        picker_add_hour.wrapSelectorWheel = false
        picker_add_hour.value = subjectData?.studyTime?.hour ?: 0
        picker_add_minute.wrapSelectorWheel = false
        picker_add_minute.value = subjectData?.studyTime?.minute ?: 0

        edit_add_label.setText(subjectData?.name)

        image = subjectData?.icon ?: IconEnum.Subject0

        var subjectColor = subjectData?.color ?: getColor(R.color.accentedGray)

        text_popup_edit5.setOnClickListener {
            val sheet = BottomSheetDialog(this, R.style.Theme_Naptech_BottomSheet)
            val view = layoutInflater.inflate(R.layout.bottom_select, null)

            sheet.behavior.peekHeight = 0
            sheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            sheet.behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) sheet.dismiss()
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) { }
            })

            view.apply {
                listOf(IconEnum.Subject0 to image_subject_0, IconEnum.Subject1 to image_subject_1, IconEnum.Subject2 to image_subject_2, IconEnum.Subject3 to image_subject_3, IconEnum.Subject4 to image_subject_4).forEach {
                    it.second.setOnClickListener { _ ->
                        image = it.first
                        sheet.dismiss()
                    }
                }
                val colorViewList = listOf(getColor(R.color.accentedRed) to view_red, getColor(R.color.accentedOrange) to view_orange, getColor(R.color.accentedYellow) to view_yellow, getColor(R.color.accentedGreen) to view_green, getColor(R.color.accentedBlue) to view_blue, getColor(R.color.accentedViolet) to view_violet, getColor(R.color.accentedGray) to view_gray)
                colorViewList.forEach {
                    it.second.setOnClickListener { _ ->
                        colorViewList.find { l -> l.first == subjectColor }?.second?.apply {
                            setBackgroundResource(R.drawable.rectangle_16)
                        }
                        it.second.setBackgroundResource(R.drawable.ic_complete)
                        subjectColor = it.first
                    }
                }
                colorViewList.find { it.first == subjectColor }?.second?.setBackgroundResource(R.drawable.ic_complete)
            }

            sheet.setContentView(view)
            sheet.show()
        }

        if (subjectData != null) text_popup_edit4.setOnClickListener {
            var key: String? = null
            val json = Json { allowStructuredMapKeys = true }.encodeToString(subjectData)
            database.forEach { // TODO("개선 필요")
                if (it.value.toString() == json) {
                    key = it.key
                    return@forEach
                }
            }
            database.remove(key)
            finish()
        } else text_popup_edit4.visibility = View.GONE

        layout_add_button.setOnClickListener {
            val time = SerializableTime(picker_add_hour.value, picker_add_minute.value, 0)
            val key = if (data == null) NaptechUtil.timestamp.toString() else {
                var key: String? = null
                val json = Json { allowStructuredMapKeys = true }.encodeToString(subjectData)
                database.forEach { // TODO("개선 필요")
                    if (it.value.toString() == json) {
                        key = it.key
                        return@forEach
                    }
                }
                key ?: NaptechUtil.timestamp.toString()
            }
            database[key] = Json { allowStructuredMapKeys = true }.encodeToString(SubjectData(edit_add_label.text.toString(), time, image, subjectColor, subjectData?.log ?: mapOf()))
            finish()
        }
    }
}