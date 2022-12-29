package com.poemcollection.utils

import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal

private const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

fun Temporal.toDatabaseString(): String {
    val formatter = DateTimeFormatter.ofPattern(DATE_FORMAT)
    return formatter.format(this)
}
