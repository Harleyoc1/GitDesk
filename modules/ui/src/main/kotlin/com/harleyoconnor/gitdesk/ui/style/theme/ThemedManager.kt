package com.harleyoconnor.gitdesk.ui.style.theme

import com.harleyoconnor.gitdesk.ui.style.theme.ThemedManager.register
import com.harleyoconnor.gitdesk.util.schedule
import com.harleyoconnor.gitdesk.util.system.SystemManager
import java.time.Duration

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

    init {
        schedule(this::update, Duration.ofSeconds(1))
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
     * Updates any registered [themed] by calling [Themed.update] if the
     * [SystemManager.Theme] has changed since the last query.
     */
    private fun update() {
        val lastTheme = SystemManager.get().getLastTheme()
        val newTheme = SystemManager.get().getTheme()

        if (lastTheme != newTheme) {
            updateAll(lastTheme, newTheme)
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