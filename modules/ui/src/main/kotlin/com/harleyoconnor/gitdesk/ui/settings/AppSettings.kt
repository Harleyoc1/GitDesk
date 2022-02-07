package com.harleyoconnor.gitdesk.ui.settings

import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.settings.JsonSettings
import com.harleyoconnor.gitdesk.data.settings.Settings
import com.harleyoconnor.gitdesk.ui.style.theme.ThemedManager
import com.harleyoconnor.gitdesk.util.system.SystemManager
import com.squareup.moshi.Json
import java.io.File

class AppSettings : Settings.SettingsData {

    companion object {
        private val settings = JsonSettings<AppSettings>(
            MOSHI.adapter(AppSettings::class.java),
            AppSettings(),
            File(SystemManager.get().getAppDataLocation() + File.separatorChar + "Settings.json")
        )

        fun get(): Settings<AppSettings> = settings
    }

    val appearance: Appearance
    val integrations: Integrations
    val repositories: Repositories

    constructor(
        appearance: Appearance = Appearance(),
        integrations: Integrations = Integrations(),
        repositories: Repositories = Repositories()
    ) {
        this.appearance = appearance
        this.integrations = integrations
        this.repositories = repositories
    }

    constructor(other: AppSettings) {
        appearance = other.appearance
        integrations = other.integrations
        repositories = other.repositories
    }

    class Appearance(
        var theme: ThemeSelection = ThemeSelection.AUTO
    ) : Settings.SettingsData {
        enum class ThemeSelection {
            @Json(name = "light") LIGHT,
            @Json(name = "dark") DARK,
            /** Attempt to match system theme if possible. Otherwise fallback to light mode. */
            @Json(name = "auto") AUTO
        }

        override fun onSaved() {
            if (theme == ThemeSelection.AUTO) {
                ThemedManager.startTimer()
            } else {
//                ThemedManager.forceTheme(theme)
            }
        }
    }

    class Integrations(
        @Json(name = "default_external_editor") var defaultExternalEditor: File? = null
    ) : Settings.SettingsData {

        override fun onSaved() {
            TODO("Not yet implemented")
        }

    }

    class Repositories(
        @Json(name = "show_hidden_files_by_default") var showHiddenFilesByDefault: Boolean = false
    ) : Settings.SettingsData {

        override fun onSaved() {
            TODO("Not yet implemented")
        }

    }

    override fun onSaved() {
        appearance.onSaved()
        integrations.onSaved()
        repositories.onSaved()
    }

}