package com.harleyoconnor.gitdesk.ui.form.validation

import com.harleyoconnor.gitdesk.ui.util.parseIntRange
import com.harleyoconnor.gitdesk.util.Patterns
import java.util.regex.Pattern

typealias FieldValidatorFactory = (Array<String>) -> FieldValidator

/**
 *
 * @author Harley O'Connor
 */
object FieldValidators {

    val EMAIL: FieldValidator = RegexFieldValidator(Patterns.EMAIL, "validation.invalid_email")

    private val validators: Map<String, FieldValidatorFactory> = mapOf(
        "Length" to { arguments -> constructLengthValidator(arguments[0]) },
        "Regex" to { arguments -> constructRegexValidator(arguments[0], arguments[1]) },
        "Email" to { EMAIL },
        "UsernameAvailable" to { UsernameAvailableValidator },
        "LocationFree" to { LocationFreeValidator }
    )

    fun getValidator(id: String, arguments: Array<String>): FieldValidator? {
        return validators[id]?.invoke(arguments)
    }

    private fun constructLengthValidator(range: String): LengthFieldValidator {
        val intRange = parseIntRange(range)
        return LengthFieldValidator(intRange.min, intRange.max)
    }

    private fun constructRegexValidator(regex: String, invalidReasonKey: String) =
        RegexFieldValidator(Pattern.compile(regex), invalidReasonKey)

}