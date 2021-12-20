package com.harleyoconnor.gitdesk.data.account

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.util.create
import com.harleyoconnor.gitdesk.util.system.SystemManager
import com.squareup.moshi.Json
import com.squareup.moshi.JsonAdapter
import org.apache.logging.log4j.LogManager
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
class Session(
    @Json(name = "session_key") val key: String
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

        fun delete() {
            load()?.let {
                deleteSessionRequest(it).join()
                    .ifError { code ->
                        LogManager.getLogger().error(
                            "Could not delete session from server: received failure HTTP response code $code."
                        )
                    }
                File(FILE_PATH).delete()
            }
        }
    }

    fun save() {
        File(FILE_PATH).create().writeText(ADAPTER.toJson(this))
    }

    fun getAccount(): Account? {
        val response = getAccountRequest(this).join()
        if (response.wasError()) {
            LogManager.getLogger().error("Error retrieving account data: ${response.getCode()}.")
        }
        return response.get()
    }

}