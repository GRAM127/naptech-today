package com.naptech.today.main.study

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.naptech.today.R
import com.naptech.today.func.NaptechActivity
import com.naptech.today.func.NaptectFragment
import com.naptech.today.main.schedule.ScheduleFragment
import com.naptech.today.util.SerializableDate
import kotlinx.android.synthetic.main.fragment_study.*
import java.time.LocalDate


class StudyFragment : NaptectFragment() {

    private lateinit var viewModel: StudyViewModel
    var uid: String? = null; private set
    private var date: SerializableDate? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel = ViewModelProvider(it, ViewModelProvider.NewInstanceFactory()).get(StudyViewModel::class.java)
        }
        arguments?.let {
            uid = it.getString(UID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_study, container, false)
    }

    override fun onStart() {
        super.onStart()
        if (activity is NaptechActivity) uid?.let {
            list_main_study.layoutManager = LinearLayoutManager(context)
            list_main_study.adapter = viewModel.getStudyAdapter(activity as NaptechActivity, it, date?.localDate ?: LocalDate.now())
        }
        list_main_study.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState in 0..1 && activity is ScheduleFragment.ScrollListener) (activity as ScheduleFragment.ScrollListener).onScrollState(newState == 1)
            }
        })
    }

    fun setDate(date: LocalDate) {
        this.date = SerializableDate(date)
        list_main_study?.adapter = viewModel.getStudyAdapter(activity as NaptechActivity, uid!!, date)
    }

    companion object {
        private const val UID = "uid"
        fun newInstance(uid: String?) = StudyFragment().apply {
            arguments = Bundle().apply {
                putString(UID, uid)
            }
        }
    }
}