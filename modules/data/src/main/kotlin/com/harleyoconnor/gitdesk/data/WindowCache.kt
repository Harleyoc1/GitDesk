package com.harleyoconnor.gitdesk.data

import com.squareup.moshi.Json

class WindowCache(
    val x: Double = 100.0,
    val y: Double = 100.0,
    val width: Double = 600.0,
    val height: Double = 450.0,
    @Json(name = "full_screen") val fullScreen: Boolean = false
)