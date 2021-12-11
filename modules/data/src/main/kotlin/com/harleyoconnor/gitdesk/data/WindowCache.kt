package com.harleyoconnor.gitdesk.data

import com.squareup.moshi.Json

class WindowCache(
    val x: Double = 0.0,
    val y: Double = 0.0,
    val width: Double = 0.0,
    val height: Double = 0.0,
    @Json(name = "full_screen") val fullScreen: Boolean = false
)