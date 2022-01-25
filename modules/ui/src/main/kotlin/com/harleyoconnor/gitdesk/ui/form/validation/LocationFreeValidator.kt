package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import java.io.File

/**
 *
 * @author Harley O'Connor
 */
object LocationFreeValidator : FieldValidator {

    override fun validate(text: String) {
        val file = File(text)
        if (file.exists() && (!file.isDirectory || file.list().isNotEmpty())) {
            throw FieldValidator.InvalidException(getReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString("validation.location.not_free")
}