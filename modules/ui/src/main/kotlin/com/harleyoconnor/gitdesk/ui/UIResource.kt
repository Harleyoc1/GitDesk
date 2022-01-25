package com.harleyoconnor.gitdesk.ui

import com.harleyoconnor.gitdesk.util.Resource
import java.io.FileNotFoundException
import java.io.InputStream
import java.net.URL

/**
 * @author Harley O'Connor
 */
data class UIResource(
    override val path: String
) : Resource {

    override val stream: InputStream
        get() = this.javaClass.getResourceAsStream(path)
            ?: throw FileNotFoundException("Could not find app resource with path \"$path\".")

    override val location: URL
        get() = this.javaClass.getResource(path)
            ?: throw FileNotFoundException("Could not find app resource with path \"$path\".")

}