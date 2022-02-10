package com.harleyoconnor.gitdesk.util

import java.util.regex.Pattern

/**
 * @author Harley O'Connor
 */
object Patterns {

    const val ACCEPTABLE_URL_PATH_RANGE = "A-Za-z0-9-._~:/?#\\[\\]@!\$&'()*+,;%="

    val URL: Pattern by lazy { Pattern.compile("http(s?)://((?=.*\\.)[a-zA-Z0-9.-]+)(/[$ACCEPTABLE_URL_PATH_RANGE]*)?") }

    /** Fully [RFC 5322](https://www.ietf.org/rfc/rfc5322.txt) compliant email pattern. Taken from [this StackOverflow answer](https://stackoverflow.com/a/201378/6876001). */
    val EMAIL: Pattern by lazy { Pattern.compile("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])") }

    /** Password pattern that requires 12-64 characters and at least one number and symbol. */
    val PASSWORD: Pattern by lazy { Pattern.compile("(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[/\\[\\\$-/:-?{-~!\\\"^_`\\[\\]\\]\\/]).{12,64}") }

    fun createUrlPattern(domain: String): Pattern {
        return Pattern.compile("http(s?)://((?=.*\\.)[a-zA-Z0-9.-]+)(/[$ACCEPTABLE_URL_PATH_RANGE]*)?")
    }

}