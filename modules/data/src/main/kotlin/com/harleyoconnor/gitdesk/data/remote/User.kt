package com.harleyoconnor.gitdesk.data.remote

import java.net.URL

/**
 * @author Harley O'Connor
 */
interface User {

    /** The user's username on this platform. */
    val username: String

    /** The URL for the user's profile page on this platform. */
    val url: URL

    /** The URL for the user's avatar on this platform. */
    val avatarUrl: URL

}