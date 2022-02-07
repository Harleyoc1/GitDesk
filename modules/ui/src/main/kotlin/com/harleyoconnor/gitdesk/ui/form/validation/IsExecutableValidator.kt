package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import java.io.File

/**
 * Validates that the file at the entered path can be executed.
 *
 * @author Harley O'Connor
 */
object IsExecutableValidator : FieldValidator {

    override fun validate(text: String) {
        if (!File(text).canExecute()) {
            throw FieldValidator.InvalidException(getReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString("validation.executable.not")

}