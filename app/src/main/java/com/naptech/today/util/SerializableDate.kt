package com.naptech.today.util

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalTime

@Serializable
data class SerializableDate(val year: Int, val month: Int, val dayOfMonth: Int) {
    constructor(localDate: LocalDate): this(localDate.year, localDate.month.value, localDate.dayOfMonth)
    val localDate: LocalDate; get() = LocalDate.of(year, month, dayOfMonth)
}
