package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE

/**
 *
 * @author Harley O'Connor
 */
class MatchesFieldValidator(
    private val supplier: () -> String,
    private val invalidReasonKey: String
): FieldValidator {

    override fun validate(text: String) {
        if (this.supplier() != text) {
            throw FieldValidator.InvalidException(getReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString(invalidReasonKey)
}