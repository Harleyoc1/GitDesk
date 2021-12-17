package com.harleyoconnor.gitdesk.data.account

import com.squareup.moshi.Json

/**
 *
 * @author Harley O'Connor
 */
class Session(
    @Json(name = "session_key") val sessionKey: String
)