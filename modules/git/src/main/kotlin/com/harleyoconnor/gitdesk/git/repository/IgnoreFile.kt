package com.harleyoconnor.gitdesk.git.repository

import java.util.regex.Pattern

interface IgnoreFile {

    /**
     * The current list of ignore rules in place.
     */
    val rules: List<Rule>

    fun add(rule: Rule)

    fun add(body: String)

    fun edit(rule: Rule, newBody: String)

    fun remove(rule: Rule)

    fun refresh()

    interface Rule {
        val body: String
    }

    class CustomRule(
        override val body: String
    ) : Rule

    /**
     * A rule that ignores a certain directory name. The body is generally in the format `<path>/` (where path may
     * include wildcards).
     */
    class DirectoryRule(
        override val body: String
    ) : Rule {
        companion object {
            private val pattern: Pattern = Pattern.compile(
                ".*/\\*{0,2}"
            )
            fun isOfType(rule: String): Boolean = pattern.matcher(rule).matches()

        }

    }

    /**
     * A rule that ignores a certain file extension. The body is always in the format `*.<extension>`.
     */
    class FileExtensionRule(
        override val body: String
    ) : Rule {
        companion object {
            private val pattern: Pattern = Pattern.compile(
                "\\*{0,2}\\.[a-z.]+"
            )

            fun isOfType(rule: String): Boolean = pattern.matcher(rule).matches()
        }
    }
}