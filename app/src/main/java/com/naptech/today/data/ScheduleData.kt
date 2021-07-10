package com.naptech.today.data

import com.naptech.today.enum.ScheduleStatus
import com.naptech.today.util.SerializableDateTime
import kotlinx.serialization.Serializable

@Serializable
data class ScheduleData(val time: SerializableDateTime, val title: String, val text: String, val tag: TagData?, val status: ScheduleStatus = ScheduleStatus.None)