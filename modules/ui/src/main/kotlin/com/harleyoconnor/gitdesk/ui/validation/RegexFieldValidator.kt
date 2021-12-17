package com.harleyoconnor.gitdesk.ui.validation

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import java.util.regex.Pattern

/**
 * Checks the field's text matches the regex [pattern].
 *
 * @author Harley O'Connor
 */
class RegexFieldValidator(
    private val pattern: Pattern,
    private val invalidReasonKey: String
) : FieldValidator {

    override fun validate(text: String) {
        if (!pattern.matcher(text).matches()) {
            throw FieldValidator.InvalidException(getReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString(invalidReasonKey)

}