package com.harleyoconnor.gitdesk.data.account

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.remote.Platform
import com.harleyoconnor.gitdesk.data.remote.PlatformAccount
import com.harleyoconnor.gitdesk.data.remote.User
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

        private var SESSION: Session? = null

        fun getOrLoad(): Session? {
            if (SESSION == null) {
                SESSION = load()
            }
            return SESSION
        }

        private fun load(): Session? {
            val file = File(FILE_PATH)
            return if (file.exists())
                ADAPTER.fromJson(file.readText())
            else null
        }

    }

    fun save() {
        File(FILE_PATH).create().writeText(ADAPTER.toJson(this))
    }

    fun delete() {
        deleteSessionRequest(this).join()
            .ifError { code ->
                LogManager.getLogger().error(
                    "Could not delete session from server: received failure HTTP response code $code."
                )
            }
        File(FILE_PATH).delete()
        SESSION = null
    }

    fun getAccount(): Account? {
        val response = getAccountRequest(this).join()
        if (response.wasError()) {
            LogManager.getLogger().error("Error retrieving account data: ${response.getCode()}.")
        }
        return response.get()
    }

    fun getLinkFor(platform: Platform): PlatformAccount? {
        return when (platform) {
            Platform.GITHUB -> getGitHubAccount()
            else -> null
        }
    }

    fun getUserFor(link: PlatformAccount): User? {
        return link.platform.networking?.getUser(link.username)
    }

    fun getUserFor(platform: Platform): User? {
        return getLinkFor(platform)?.let {
            getUserFor(it)
        }
    }

    fun getGitHubAccount(): GitHubAccount? {
        val response = getGitHubAccountRequest(this).join()
        // 404 means no GitHub account is linked to this user, so don't log error.
        if (response.getCode() == 404) {
            return null
        }
        if (response.wasError()) {
            LogManager.getLogger().error("Error retrieving GitHub account data: ${response.getCode()}.")
        }
        return response.get()
    }

}