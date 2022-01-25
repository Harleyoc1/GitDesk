package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.git.repository.Repository
import com.harleyoconnor.gitdesk.ui.translation.TRANSLATIONS_BUNDLE

/**
 * @property currentName if editing the name of the branch, this stores the current name and the field will be
 * considered valid if the name stays the same
 * @author Harley O'Connor
 */
class BranchNameAvailableValidator(
    private val repository: Repository,
    private val currentName: String? = null
) : FieldValidator {

    override fun validate(text: String) {
        if (repository.hasBranch(text) && text != currentName) {
            throw FieldValidator.InvalidException(TRANSLATIONS_BUNDLE.getString("validation.branch_name.taken"))
        }
    }
}