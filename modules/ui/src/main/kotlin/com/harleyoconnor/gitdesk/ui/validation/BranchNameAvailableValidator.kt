package com.harleyoconnor.gitdesk.ui.validation

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE

/**
 * @author Harley O'Connor
 */
class BranchNameAvailableValidator(
    private val repository: Repository
): FieldValidator {

    override fun validate(text: String) {
        if (repository.hasBranch(text)) {
            throw FieldValidator.InvalidException(TRANSLATIONS_BUNDLE.getString("validation.branch_name.taken"))
        }
    }
}