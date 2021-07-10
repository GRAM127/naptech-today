package com.naptech.today.util

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.ZoneId

@Serializable
data class SerializableDateTime(val year: Int, val month: Int, val dayOfMonth: Int, val hour: Int, val minute: Int) {
    constructor(localDateTime: LocalDateTime): this(localDateTime.year, localDateTime.month.value, localDateTime.dayOfMonth, localDateTime.hour, localDateTime.minute)
    constructor(date: SerializableDate, time: SerializableTime): this(date.year, date.month, date.dayOfMonth, time.hour, time.minute)
    val localDateTime: LocalDateTime; get() = LocalDateTime.of(year, month, dayOfMonth, hour, minute)
    val timestamp: Long; get() = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
}
