package com.harleyoconnor.gitdesk.ui.style

/**
 *
 * @author Harley O'Connor
 */
interface StylesheetManager {

    fun registerSheets(vararg stylesheets: Stylesheet)

    fun unregisterSheets(vararg stylesheets: Stylesheet)

    fun unregisterAllSheets()

}