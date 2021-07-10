package com.naptech.today.main.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naptech.today.*
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.NaptectFragment
import com.naptech.today.func.firebase.FirebaseDatabaseMap
import kotlinx.android.synthetic.main.fragment_schedule.*
import java.time.LocalDateTime


class ScheduleFragment : NaptectFragment() {

    private lateinit var viewModel: ScheduleViewModel
    private var selectDateTime: LocalDateTime? = null
    var uid: String? = null; private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProvider(it, ViewModelProvider.NewInstanceFactory()).get(ScheduleViewModel::class.java)
        }
        arguments?.let {
            uid = it.getString(UID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (activity is NaptechActivity) {
            list_main_schedule.layoutManager = LinearLayoutManager(context)
            uid?.let {
                list_main_schedule.adapter = viewModel.getScheduleAdapter(activity as NaptechActivity, it, object: FirebaseDatabaseMap.OnUpdateListener {
                    override fun onAdded(key: String, newData: Any?) {
                        if (selectDateTime == null) smoothScrollToPosition(LocalDateTime.now())
                    }
                })
            }
            list_main_schedule.addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState in 0..1 && activity is ScrollListener) (activity as ScrollListener).onScrollState(newState == 1)
                }
            })
        }
    }

    fun setDateTime(dateTime: LocalDateTime) {
        if (smoothScrollToPosition(dateTime)) selectDateTime = dateTime
    }

    private fun smoothScrollToPosition(dateTime: LocalDateTime): Boolean {
        val pos = viewModel.findIndex(dateTime)
        return if (pos >= 0) {
            list_main_schedule?.smoothScrollToPosition(pos); true
        } else false
    }

    interface ScrollListener {
        fun onScrollState(isScroll: Boolean)
    }

    companion object {
        private const val UID = "uid"
        fun newInstance(uid: String?) = ScheduleFragment().apply {
            arguments = Bundle().apply {
                putString(UID, uid)
            }
        }
    }
}