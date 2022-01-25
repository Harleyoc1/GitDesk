package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE

/**
 * Checks the length of the field's text is within [min] and [max].
 *
 * @author Harley O'Connor
 */
class LengthFieldValidator(
    private val min: Int,
    private val max: Int
) : FieldValidator {

    override fun validate(text: String) {
        if (text.length !in min .. max) {
            throw FieldValidator.InvalidException(this.getReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString("validation.length.out_of_range")
        .replaceFirst("{}", min.toString())
        .replaceFirst("{}", max.toString())
}