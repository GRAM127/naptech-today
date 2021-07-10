package com.naptech.today.util

import android.os.Parcel
import android.os.Parcelable
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

@Serializable
data class SerializableTime(val hour: Int, val minute: Int, val second: Int, val nano: Int = 0) {
    constructor(localTime: LocalTime): this(localTime.hour, localTime.minute, localTime.second, localTime.nano)
    val localTime: LocalTime; get() = LocalTime.of(hour, minute, second)
}
