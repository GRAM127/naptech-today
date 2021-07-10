package com.naptech.today.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.naptech.today.R
import com.naptech.today.data.SubjectData
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.firebase.FirebaseLogin
import com.naptech.today.util.SerializableDate
import com.naptech.today.util.SerializableTime
import kotlinx.android.synthetic.main.activity_account.*
import kotlinx.android.synthetic.main.activity_account.chart_account_hours_studied
import kotlinx.android.synthetic.main.activity_study_popup.*
import kotlinx.android.synthetic.main.bottom_login.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Duration
import java.time.LocalDate

class AccountActivity : NaptechActivity() {

    private val firebaseLogin = FirebaseLogin(this, ::onLogin)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val list = Json { allowStructuredMapKeys = true }.decodeFromString<List<SubjectData>>(intent.getStringExtra("list").toString())

        firebaseLogin.getInstance()
        if (firebaseLogin.user == null) firebaseLogin.login(FirebaseLogin.LoginOption.ANONYMOUSLY)

        if (firebaseLogin.user?.isAnonymous == false) layout_account_login.visibility = View.GONE
        else {
            with (layout_account_login) {
                card_bottom_login_google.setOnClickListener { firebaseLogin.login(FirebaseLogin.LoginOption.GOOGLE) }
                card_bottom_login_apple.setOnClickListener { firebaseLogin.login(FirebaseLogin.LoginOption.APPLE) }
            }
        }

        if (firebaseLogin.user?.isAnonymous == true) text_account_sign_out.visibility = View.GONE
        else text_account_sign_out.setOnClickListener {
            firebaseLogin.logout()
            finish()
        }

        Glide.with(this).load(firebaseLogin.user?.photoUrl).error(R.drawable.image_person).circleCrop().centerCrop().into(image_account_user)

        text_account_name.text = firebaseLogin.user?.displayName

        if (list.isNotEmpty()) {
            val entryList = mutableListOf<BarEntry>()
            for (i in 0..9) {
                val floatArray = FloatArray(list.size)
                val nameList = mutableListOf<String>()
                list.forEachIndexed { index, subjectData ->
                    var t = 0f
                    subjectData.log[SerializableDate(
                        LocalDate.now().plusDays(-i.toLong())
                    )].let { list ->
                        list?.forEach {
                            t += Duration.between(
                                it.start.localDateTime,
                                it.end.localDateTime
                            ).toMinutes()
                        }
                    }
                    floatArray[index] = t
                    nameList.add(subjectData.name)
                }
                entryList.add(BarEntry(10 - i.toFloat(), floatArray))
            }
            val barDataSet = BarDataSet(entryList, "")
            barDataSet.stackLabels = list.map { it.name }.toTypedArray()
            barDataSet.colors = list.map { it.color }
            chart_account_hours_studied.apply {
                setScaleEnabled(true)
                setPinchZoom(false)
                animateXY(0, 800)
                axisLeft.axisMinimum = 0f
                axisRight.axisMinimum = 0f
                axisRight.setDrawLabels(false)
                xAxis.setDrawGridLines(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.labelCount = 10
                xAxis.valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val d = LocalDate.now().plusDays(value.toLong() - 10)
                        return "${d.dayOfMonth}일"
                    }
                }
                chart_account_hours_studied.data = BarData(barDataSet)
            }
        }
    }

    private fun onLogin(isSuccessful: Boolean) {
        if (isSuccessful) finish()
    }
}