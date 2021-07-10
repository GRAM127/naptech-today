package com.naptech.today.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.FirebaseDatabase
import com.naptech.today.account.AccountActivity
import com.naptech.today.R
import com.naptech.today.study.StudyActivity
import com.naptech.today.schedule.ScheduleAddActivity
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import com.naptech.today.func.firebase.FirebaseLogin
import com.naptech.today.main.schedule.ScheduleFragment
import com.naptech.today.main.study.StudyFragment
import com.naptech.today.main.study.StudyViewModel
import com.naptech.today.study.StudyAddActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_login.view.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.*

class MainActivity : NaptechActivity(), ScheduleFragment.ScrollListener {

    private val firebaseLogin = FirebaseLogin(this, ::onLogin)
    private val fragmentManager by lazy { FragmentManager(layout_main_fragment.id) }

    private var scheduleFragment: ScheduleFragment? = null
    private var studyFragment: StudyFragment? = null

    private var sheet: BottomSheetDialog? = null
    private var oldUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        firebase 로그인 설정
        firebaseLogin.getInstance()
//        TODO("네트워크 오류 대응할 것")
        if (firebaseLogin.user == null) firebaseLogin.login(FirebaseLogin.LoginOption.ANONYMOUSLY)
        else onLogin(true)

        firebaseLogin.user?.let {
            val reference = FirebaseDatabase.getInstance().reference.child("study").child(it.uid)
            FirebaseDatabaseMap(reference).start().setOnUpdateListener(object : FirebaseDatabaseMap.OnUpdateListener {
                override fun onAdded(key: String, newData: Any?) {
                    if (key == "status" && newData != null) {
                        val intent = Intent(this@MainActivity, StudyActivity::class.java)
                        intent.putExtra("data", newData.toString())
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }

//        프래그먼트 설정
        scheduleFragment = ScheduleFragment.newInstance(firebaseLogin.user?.uid)
        studyFragment = StudyFragment.newInstance(firebaseLogin.user?.uid)
        fragmentManager.replace(scheduleFragment!!)

//        메뉴 설정
        button_main_schedule.setOnClickListener {
            if (button_main_schedule.backgroundTintList == getColorStateList(R.color.layout_menu_deselected)) {
                button_main_schedule.backgroundTintList = getColorStateList(R.color.layout_menu_selected)
                button_main_schedule.elevation = 16f
                button_main_study.backgroundTintList = getColorStateList(R.color.layout_menu_deselected)
                button_main_study.elevation = 0f
                
                fragmentManager.replace(scheduleFragment!!) // TODO("애니메이션")

                text_main_button.text = "일정 추가" // TODO("애니메이션")
            }
        }
        button_main_study.setOnClickListener {
            if (button_main_study.backgroundTintList == getColorStateList(R.color.layout_menu_deselected)) {
                button_main_study.backgroundTintList = getColorStateList(R.color.layout_menu_selected)
                button_main_study.elevation = 16f
                button_main_schedule.backgroundTintList = getColorStateList(R.color.layout_menu_deselected)
                button_main_schedule.elevation = 0f

                fragmentManager.replace(studyFragment!!) // TODO("애니메이션")

                text_main_button.text = "과목 추가"
            }
        }


//        user 버튼 설정
        val studyViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(StudyViewModel::class.java)
        studyViewModel.getStudyAdapter(this, firebaseLogin.user!!.uid, LocalDate.now())
        image_main_user.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            val list = studyViewModel.getSubjectList()
            intent.putExtra("list", Json { allowStructuredMapKeys = true }.encodeToString(list))
            startActivity(intent)
        }

//        하단 버튼 클릭 리스너 설정
        layout_main_button.setOnClickListener {
            startActivity(Intent(this, if (text_main_button.text == "일정 추가" /* TODO("임시") */) ScheduleAddActivity::class.java else StudyAddActivity::class.java))
        }

//        날짜 선택 view 설정
        val adapter = CalendarAdapter(this).apply {
            setOnSelectedListener { date, pos ->
                setDateIndicator(date)
                with(list_main_date_indicator) {
                    val position = pos - 1
                    val firstItem = getChildLayoutPosition(getChildAt(0))
                    val lastItem = getChildLayoutPosition(getChildAt(childCount - 1))
                    if (position in firstItem..lastItem) {
                        val movePos = position - firstItem
                        if (movePos in 0 until childCount) {
                            smoothScrollBy(getChildAt(movePos).left, 0, OvershootInterpolator())
                        }
                    } else smoothScrollToPosition(position)
                }
            }
        }
        list_main_date_indicator.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        list_main_date_indicator.adapter = adapter
        list_main_date_indicator.layoutManager?.scrollToPosition(Int.MAX_VALUE / 2 - 1)
    }

    override fun onStart() {
        super.onStart()
        setButtonShow(true)
        setDateIndicator(LocalDate.now())
    }

    override fun onResume() {
        super.onResume()
        if (oldUid != firebaseLogin.user?.uid) if (firebaseLogin.user?.uid == null) firebaseLogin.login(FirebaseLogin.LoginOption.ANONYMOUSLY) else onLogin(true)
    }


    @SuppressLint("SetTextI18n")
    private fun setDateIndicator(date: LocalDate) {
        text_main_date.text = "${date.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.ENGLISH)}, ${date.dayOfMonth}"
        text_main_week.text = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

        val dateTime = if (LocalDate.now() == date) LocalDateTime.now() else LocalDateTime.of(date, LocalTime.MIN)
        if (scheduleFragment?.uid != null) if (scheduleFragment?.uid == firebaseLogin.user?.uid) scheduleFragment?.setDateTime(dateTime)
        if (studyFragment?.uid != null) if (studyFragment?.uid == firebaseLogin.user?.uid) studyFragment?.setDate(date)
    }

    private fun setButtonShow(isShow: Boolean) {
        layout_main_button?.animate()?.apply {
            translationY(if (isShow) 0f else layout_main_button.height.toFloat() + 50f)
            withLayer()
            interpolator = AnticipateOvershootInterpolator()
            duration = 200
        }
    }

    override fun onScrollState(isScroll: Boolean) {
        setButtonShow(!isScroll)
    }

//    ------------

    private fun onLogin(isSuccessful: Boolean) {
        if (!isSuccessful) return
        if (firebaseLogin.user?.isAnonymous == true) {
            sheet = BottomSheetDialog(this, R.style.Theme_Naptech_BottomSheet)
            val view = layoutInflater.inflate(R.layout.bottom_login, null)
            sheet!!.behavior.peekHeight = 0
            sheet!!.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            sheet!!.behavior.addBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) sheet!!.dismiss()
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) { }
            })
            with (view) {
                card_bottom_login_google.setOnClickListener { firebaseLogin.login(FirebaseLogin.LoginOption.GOOGLE) }
                card_bottom_login_apple.setOnClickListener { firebaseLogin.login(FirebaseLogin.LoginOption.APPLE) }
            }
            sheet!!.setContentView(view)
            sheet!!.show()
        } else sheet?.dismiss()
        Glide.with(this).load(firebaseLogin.user?.photoUrl).error(R.drawable.image_person).centerCrop().circleCrop().into(image_main_user)
        scheduleFragment = ScheduleFragment.newInstance(firebaseLogin.user?.uid)
        studyFragment = StudyFragment.newInstance(firebaseLogin.user?.uid)
        fragmentManager.replace(if (text_main_button.text == "일정 추가" /* TODO("임시") */) scheduleFragment!! else studyFragment!!)
        oldUid = firebaseLogin.user?.uid
    }
}