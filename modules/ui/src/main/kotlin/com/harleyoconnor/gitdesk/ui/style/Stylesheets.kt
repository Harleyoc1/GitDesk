package com.harleyoconnor.gitdesk.ui.style

/**
 * @author Harley O'Connor
 */
object Stylesheets {

    val DEFAULT: Stylesheet by lazy {
        StaticStylesheet("/ui/stylesheets/default.css")
    }

    val DEFAULT_THEMED: Stylesheet by lazy {
        ThemedStylesheet.fromBasePath("/ui/stylesheets/default")
    }

    val REPOSITORY: Stylesheet by lazy {
        StaticStylesheet("/ui/stylesheets/repository.css")
    }

}