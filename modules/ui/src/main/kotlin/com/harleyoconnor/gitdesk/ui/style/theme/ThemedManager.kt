package com.harleyoconnor.gitdesk.ui.style.theme

import com.harleyoconnor.gitdesk.ui.style.theme.ThemedManager.register
import com.harleyoconnor.gitdesk.util.schedule
import com.harleyoconnor.gitdesk.util.system.SystemManager
import java.time.Duration
import java.util.Timer

/**
 * Handles managing [Themed] objects, by keeping them up to date with the current
 * [SystemManager.Theme].
 *
 * To register a [Themed] object to be handled by this manager, use [register].
 *
 * @author Harley O'Connor
 */
object ThemedManager {

    private val themed: MutableSet<Themed> = mutableSetOf()

    private var updater: Timer? = null
    private var lastTheme: SystemManager.Theme = SystemManager.Theme.LIGHT

    fun getLastTheme(): SystemManager.Theme = lastTheme

    fun startTimer() {
        updater?.cancel() // Cancel timer if there is one already running.
        updater = schedule(this::update, Duration.ofSeconds(1))
    }

    fun forceTheme(theme: SystemManager.Theme) {
        updater?.cancel()
        if (lastTheme != theme) {
            updateAll(lastTheme, theme)
            lastTheme = theme
        }
    }

    /**
     * Registers the specified [Themed] object to be managed by this class.
     *
     * This means this class will handle calling [Themed.update] on the specified
     * [Themed] object via the [update] method.
     *
     * @param themed The [Themed] object to manage.
     */
    fun register(themed: Themed) {
        ThemedManager.themed.add(themed)
    }

    /**
     * Updates any registered [themed] objects by calling [Themed.update] if the theme has changed.
     */
    private fun update() {
        val newTheme = SystemManager.get().getTheme() ?: lastTheme

        if (lastTheme != newTheme) {
            updateAll(lastTheme, newTheme)
            lastTheme = newTheme
        }
    }

    private fun updateAll(
        lastTheme: SystemManager.Theme,
        newTheme: SystemManager.Theme
    ) {
        themed.sorted().forEach {
            it.update(lastTheme, newTheme)
        }
    }

}