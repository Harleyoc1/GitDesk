package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.data.account.isUsernameAvailableQuery
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.util.network.Response
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 *
 * @author Harley O'Connor
 */
object UsernameAvailableValidator: FieldValidator {

    override fun validate(text: String) {
        try {
            val response: Response<Boolean> = isUsernameAvailableQuery(text).get(1, TimeUnit.SECONDS)
            if (response.wasError()) {
                throw FieldValidator.InvalidException(getCannotCheckReason())
            }
            val available = response.get()!!
            if (!available) {
                throw FieldValidator.InvalidException(getReason())
            }
        } catch (e: TimeoutException) {
            throw FieldValidator.InvalidException(getCannotCheckReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString("validation.username.taken")

    private fun getCannotCheckReason() =
        TRANSLATIONS_BUNDLE.getString("validation.username.cannot_check_availability")

}