package com.harleyoconnor.gitdesk.util

import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset

/**
 * Represents a resource loaded from the `resources` directory, in terms of its [path].
 *
 * Implementations should provide access the resource's [stream][InputStream], since modules accessing external module
 * resources is problematic.
 *
 * @author Harley O'Connor
 */
interface Resource {

    val path: String

    val stream: InputStream

    val location: URL

    fun readLines(charset: Charset = Charsets.UTF_8): List<String> =
        this.stream.reader(charset).readLines()

}