package com.harleyoconnor.gitdesk.test.data.account

import com.harleyoconnor.gitdesk.data.account.isUsernameAvailableQuery
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
class AccountsTests {

    @Test
    fun `test checking for unavailable username`() {
        val response = isUsernameAvailableQuery("Harleyoc1").get(30, TimeUnit.SECONDS)
        assert(response.wasSuccess()) {
            throw ServerSideException("HTTP Response Code: " + response.getCode().toString())
        }
        Assertions.assertFalse(response.get()!!)
    }

    @Test
    fun `test checking for available username`() {
        // Assumes this random username will never be taken.
        val response = isUsernameAvailableQuery("4tfddfsdafdaswadxr443ddfdz").get(30, TimeUnit.SECONDS)
        assert(response.wasSuccess()) {
            throw ServerSideException("HTTP Response Code: " + response.getCode().toString())
        }
        Assertions.assertTrue(response.get()!!)
    }

    class ServerSideException(
        message: String
    ): Exception(message)

}