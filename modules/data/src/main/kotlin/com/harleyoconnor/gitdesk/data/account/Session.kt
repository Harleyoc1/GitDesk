package com.harleyoconnor.gitdesk.data.account

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.util.create
import com.harleyoconnor.gitdesk.util.system.SystemManager
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
class Session(
    @Json(name = "session_key") val sessionKey: String
) {

    companion object {
        val FILE_PATH: String = SystemManager.get().getAppDataLocation() +
                File.separatorChar + "GitDesk" +
                File.separatorChar + "Session.json"

        val ADAPTER: JsonAdapter<Session> = MOSHI.adapter(Session::class.java)

        fun load(): Session? {
            val file = File(FILE_PATH)
            return if (file.exists())
                ADAPTER.fromJson(file.readText())
            else null
        }
    }

    fun save() {
        File(FILE_PATH).create().writeText(ADAPTER.toJson(this))
    }

}