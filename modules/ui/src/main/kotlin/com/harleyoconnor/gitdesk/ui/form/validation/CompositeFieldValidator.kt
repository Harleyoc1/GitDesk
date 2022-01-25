package com.harleyoconnor.gitdesk.ui.form.validation

/**
 * A field validator that checks multiple child [validators]. This will only deem the text valid if all child validators
 * are passed.
 *
 * @author Harley O'Connor
 */
class CompositeFieldValidator(vararg validators: FieldValidator): FieldValidator {

    private val validators: Array<FieldValidator>

    init {
        this.validators = arrayOf(*validators)
    }

    override fun validate(text: String) {
        for (validator in validators) {
            validator.validate(text)
        }
    }

    override fun and(other: FieldValidator): FieldValidator {
        return CompositeFieldValidator(*validators, other)
    }
}