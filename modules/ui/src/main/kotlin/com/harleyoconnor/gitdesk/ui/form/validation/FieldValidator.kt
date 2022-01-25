package com.harleyoconnor.gitdesk.ui.form.validation

import kotlin.jvm.Throws

/**
 *
 * @author Harley O'Connor
 */
interface FieldValidator {

    @Throws(InvalidException::class)
    fun validate(text: String)

    infix fun and(other: FieldValidator): FieldValidator {
        return CompositeFieldValidator(this, other)
    }

    class InvalidException(reason: String): Exception(reason)

}