package com.harleyoconnor.gitdesk.util

import java.time.Duration
import java.util.Timer
import java.util.TimerTask

fun schedule(runnable: Runnable, period: Duration) {
    schedule(runnable, 0, period.toMillis())
}

fun schedule(runnable: Runnable, delay: Duration, period: Duration) {
    schedule(runnable, delay.toMillis(), period.toMillis())
}

fun schedule(runnable: Runnable, delay: Long, period: Long) {
    Timer().scheduleAtFixedRate(object : TimerTask() {
        override fun run() {
            runnable.run()
        }
    }, delay, period)
}