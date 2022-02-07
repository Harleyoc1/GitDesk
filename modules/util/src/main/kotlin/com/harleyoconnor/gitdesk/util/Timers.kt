package com.harleyoconnor.gitdesk.util

import java.time.Duration
import java.util.Timer
import java.util.TimerTask

fun schedule(runnable: Runnable, period: Duration): Timer {
    return schedule(runnable, 0, period.toMillis())
}

fun schedule(runnable: Runnable, delay: Duration, period: Duration): Timer {
    return schedule(runnable, delay.toMillis(), period.toMillis())
}

fun schedule(runnable: Runnable, delay: Long, period: Long): Timer {
    val timer = Timer()
    timer.scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            runnable.run()
        }
    }, delay, period)
    return timer
}