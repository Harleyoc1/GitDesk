package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE

/**
 * @author Harley O'Connor
 */
object NotEmptyValidator : FieldValidator {

    override fun validate(text: String) {
        if (text.isEmpty()) {
            throw FieldValidator.InvalidException(TRANSLATIONS_BUNDLE.getString("validation.not_empty.empty"))
        }
    }
}