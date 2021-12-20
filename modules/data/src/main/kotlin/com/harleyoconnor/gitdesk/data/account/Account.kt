package com.harleyoconnor.gitdesk.data.account

import com.harleyoconnor.gitdesk.data.MOSHI
import com.squareup.moshi.JsonAdapter

/**
 *
 * @author Harley O'Connor
 */
class Account(
    val username: String,
    val email: String
) {
    companion object {
        val ADAPTER: JsonAdapter<Account> = MOSHI.adapter(Account::class.java)
    }
}