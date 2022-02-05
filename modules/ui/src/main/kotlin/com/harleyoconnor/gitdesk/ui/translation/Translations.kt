package com.harleyoconnor.gitdesk.ui.translation

import java.util.*

private val MAIN_BUNDLE = "ui.translation.main"

val TRANSLATIONS_BUNDLE: ResourceBundle by lazy {
    getMainBundle()
}

/**
 * @return translation resource bundle for system locale, or default (UK) if one doesn't exist
 */
private fun getMainBundle() = try {
    ResourceBundle.getBundle(MAIN_BUNDLE)
} catch (e: MissingResourceException) {
    ResourceBundle.getBundle(MAIN_BUNDLE, Locale.UK)
}

fun ResourceBundle.getString(key: String, vararg arguments: String?): String {
    var string = getString(key)
    for (argument in arguments) {
        string = string.replaceFirst("{}", argument.toString())
    }
    return string
}