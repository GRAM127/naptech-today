package com.naptech.today.data

import com.naptech.today.enum.IconEnum
import com.naptech.today.util.SerializableDate
import com.naptech.today.util.SerializableDateTime
import com.naptech.today.util.SerializableTime
import kotlinx.serialization.Serializable

@Serializable
data class SubjectData(val name: String, val studyTime: SerializableTime, val icon: IconEnum, val color: Int, val log: Map<SerializableDate, List<StudyLog>> = mapOf()) {
    @Serializable
    data class StudyLog(val start: SerializableDateTime, val end: SerializableDateTime, val memo: String)
}
// SubjectData.log -> <날짜, <시간, 메모>>
