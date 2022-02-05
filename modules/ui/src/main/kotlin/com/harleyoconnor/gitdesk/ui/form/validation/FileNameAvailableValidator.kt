package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE
import com.harleyoconnor.gitdesk.util.Directory
import java.io.File

/**
 * @author Harley O'Connor
 */
class FileNameAvailableValidator(
    private val directory: Directory
) : FieldValidator {

    override fun validate(text: String) {
        if (File(directory.canonicalPath + File.separatorChar + text).exists()) {
            throw FieldValidator.InvalidException(getReason())
        }
    }

    private fun getReason() = TRANSLATIONS_BUNDLE.getString("validation.file_name.not_available")

}