package com.harleyoconnor.gitdesk.ui.settings

import com.harleyoconnor.gitdesk.data.Data
import com.harleyoconnor.gitdesk.data.MOSHI
import com.harleyoconnor.gitdesk.data.settings.JsonSettings
import com.harleyoconnor.gitdesk.data.settings.Settings
import com.harleyoconnor.gitdesk.ui.style.theme.ThemedManager
import com.harleyoconnor.gitdesk.util.system.SystemManager
import com.squareup.moshi.Json
import java.io.File

class AppSettings(
    val appearance: Appearance = Appearance(),
    val integrations: Integrations = Integrations(),
    val repositories: Repositories = Repositories()
) : Settings.SettingsData {

    companion object {
        private val settings = JsonSettings<AppSettings>(
            MOSHI.adapter(AppSettings::class.java),
            AppSettings(),
            Data.getPath("Settings.json").toFile()
        )

        fun get(): Settings<AppSettings> = settings

        fun load() {
            settings.getOrLoad()
        }
    }

    private val categories: Array<Settings.SettingsData> = arrayOf(appearance, integrations, repositories)

    class Appearance(
        var theme: ThemeSelection = ThemeSelection.SYSTEM
    ) : Settings.SettingsData {

        enum class ThemeSelection(
            private val applier: () -> Unit
        ) {
            @Json(name = "light")
            LIGHT({
                ThemedManager.forceTheme(SystemManager.Theme.LIGHT)
            }),
            @Json(name = "dark")
            DARK({
                ThemedManager.forceTheme(SystemManager.Theme.DARK)
            }),

            /** Attempt to match system theme if possible. Otherwise fallback to light mode. */
            @Json(name = "system")
            SYSTEM({
                ThemedManager.startTimer()
            });

            fun apply() {
                applier()
            }

            override fun toString() = super.toString().lowercase().replaceFirstChar { it.uppercase() }
        }

        override fun onLoad() {
            theme.apply()
        }

        override fun onSave() {
            theme.apply()
        }

        fun copy(): Appearance = Appearance(theme)
    }

    class Integrations(
        @Json(name = "default_external_editor") var defaultExternalEditor: File? = null
    ) : Settings.SettingsData {

        override fun onLoad() {
            // TODO: Implement
        }

        override fun onSave() {
            // TODO: Implement
        }

        fun copy(): Integrations = Integrations(defaultExternalEditor)
    }

    class Repositories(
        @Json(name = "show_hidden_files_by_default") var showHiddenFilesByDefault: Boolean = false
    ) : Settings.SettingsData {

        override fun onLoad() {
        }

        override fun onSave() {
        }

        fun copy(): Repositories = Repositories(showHiddenFilesByDefault)
    }

    override fun onLoad() {
        categories.forEach {
            it.onLoad()
        }
    }

    override fun onSave() {
        categories.forEach {
            it. onSave()
        }
    }

    fun copy(): AppSettings = AppSettings(appearance.copy(), integrations.copy(), repositories.copy())

}