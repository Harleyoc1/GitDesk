package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.data.remote.RemoteRepository
import com.harleyoconnor.gitdesk.data.remote.checklist.checklistExists
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * @author Harley O'Connor
 */
class ChecklistNameAvailableValidator(
    val remote: RemoteRepository
) : FieldValidator {

    override fun validate(text: String) {
        try {
            val exists = checklistExists(remote, text).get(1, TimeUnit.SECONDS)
                ?: throw FieldValidator.InvalidException(getCannotCheckReason())
            if (exists) {
                throw FieldValidator.InvalidException(getReason())
            }
        } catch (e: TimeoutException) {
            throw FieldValidator.InvalidException(getCannotCheckReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString("validation.checklist.name_taken")

    private fun getCannotCheckReason() =
        TRANSLATIONS_BUNDLE.getString("validation.checklist.cannot_check_availability")
}