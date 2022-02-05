package com.harleyoconnor.gitdesk.ui.util

import java.text.SimpleDateFormat
import java.util.Date

private val timeFormat = SimpleDateFormat("HH:mm")
private val dateFormat = SimpleDateFormat("dd MMM yyyy")
private val dateAndTimeFormat = SimpleDateFormat("HH:mm dd MMM yyyy")

fun Date.formatByTime(): String {
    return timeFormat.format(this)
}

fun Date.formatByDate(): String {
    return dateFormat.format(this)
}

fun Date.formatByDateAndTime(): String {
    return dateFormat.format(this)
}