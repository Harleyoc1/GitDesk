package com.harleyoconnor.gitdesk.data.remote

/**
 * @author Harley O'Connor
 */
interface License {

    val key: String
    val name: String

    /**
     * The SPDX identifier for this license.
     *
     * @see <a href="https://spdx.org/licenses/">SPDX License List</a>
     */
    val spdxId: String

}