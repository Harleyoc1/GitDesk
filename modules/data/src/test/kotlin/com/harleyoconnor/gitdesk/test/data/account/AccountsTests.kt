package com.harleyoconnor.gitdesk.test.data.account

import com.harleyoconnor.gitdesk.data.account.Session
import com.harleyoconnor.gitdesk.data.account.getAccountRequest
import com.harleyoconnor.gitdesk.data.account.isUsernameAvailableQuery
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf
import java.util.concurrent.TimeUnit

/**
 * @author Harley O'Connor
 */
class AccountsTests {

    @Test
    fun `test checking for unavailable username`() {
        val response = isUsernameAvailableQuery("Harleyoc1").get(30, TimeUnit.SECONDS)
        assert(response.wasSuccess()) {
            "Server error executing request. HTTP Response Code: " + response.getCode().toString()
        }
        Assertions.assertFalse(response.get()!!)
    }

    @Test
    fun `test checking for available username`() {
        // Assumes this random username will never be taken.
        val response = isUsernameAvailableQuery("4tfddfsdafdaswadxr443ddfdz").get(30, TimeUnit.SECONDS)
        assert(response.wasSuccess()) {
            "Server error executing request. HTTP Response Code: " + response.getCode().toString()
        }
        Assertions.assertTrue(response.get()!!)
    }

    @Test
    fun `test getting user details with invalid session key fails`() {
        // Actual session keys are 128 chars long, so this should always fail.
        val response = getAccountRequest(Session("cvfdfdcc")).get(30, TimeUnit.SECONDS)
        assert(response.getCode() == 401) {
            "Server error executing request. HTTP Response Code: " + response.getCode().toString()
        }
        Assertions.assertTrue(response.getCode() == 401)
    }

    @EnabledIf("com.harleyoconnor.gitdesk.test.data.account.AccountsTests#isSessionKeySet", disabledReason = "No session key property set.")
    @Test
    fun `test getting user details`() {
        val response = getAccountRequest(Session(System.getProperty("gitdesk.session_key"))).get(30, TimeUnit.SECONDS)
        assert(response.wasSuccess()) {
            "Server error executing request. HTTP Response Code: " + response.getCode().toString()
        }
        Assertions.assertTrue(response.getCode() == 200)
    }

    private fun isSessionKeySet(): Boolean = System.getProperty("gitdesk.session_key") != null

}