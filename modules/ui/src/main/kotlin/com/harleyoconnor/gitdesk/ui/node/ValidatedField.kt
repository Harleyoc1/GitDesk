package com.harleyoconnor.gitdesk.ui.node

import com.harleyoconnor.gitdesk.ui.style.ERROR_PSEUDO_CLASS
import com.harleyoconnor.gitdesk.ui.validation.FieldValidator
import com.harleyoconnor.gitdesk.ui.validation.FieldValidators
import com.harleyoconnor.gitdesk.util.concurrent.VariableTaskExecutor
import com.harleyoconnor.gitdesk.util.map
import javafx.application.Platform
import javafx.concurrent.Task
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import java.time.Duration

/**
 *
 * @author Harley O'Connor
 */
abstract class ValidatedField<N : TextField>: VBox() {

    companion object {
        @JvmStatic
        protected fun load(path: String, field: ValidatedField<*>) {
            val loader = FXMLLoader(ValidatedField::class.java.getResource(path))
            loader.setRoot(field)
            loader.setController(field)
            loader.load<VBox>()
        }
    }

    @FXML
    private lateinit var label: Label
    @FXML
    private lateinit var errorLabel: Label
    @FXML
    private lateinit var field: N

    private var validator: FieldValidator? = null

    private var lastText: String? = null
    private val validationTaskExecutor = VariableTaskExecutor(postCoolDown = Duration.ofMillis(60))

    @FXML
    private fun initialize() {
        field.textProperty().addListener { _, _, _ ->
            contentsUpdated(field.text)
        }
    }

    @Throws(FieldValidator.InvalidException::class)
    fun getText(): String {
        this.validator?.validate(field.text)
        return field.text
    }

    fun getTextUnvalidated(): String {
        return field.text
    }

    fun setText(text: String) {
        field.text = text
    }

    fun clearText() {
        field.text = ""
    }

    fun getLabelText(): String {
        return label.text
    }

    fun setLabelText(text: String) {
        label.text = text
    }

    fun getPromptText(): String {
        return field.promptText
    }

    fun setPromptText(text: String) {
        field.promptText = text
    }

    fun getFieldPrefHeight(): Double {
        return field.prefHeight
    }

    fun setFieldPrefHeight(value: Double) {
        field.prefHeight = value
    }

    fun getValidation(): String = ""

    fun setValidation(validation: String) {
        validation.split(delimiters = arrayOf("; ")).forEach { validatorString ->
            setOrAppendValidator(parseValidator(validatorString))
        }
    }

    private fun parseValidator(validatorString: String): FieldValidator {
        val idAndArgs = validatorString.split(' ')
        return FieldValidators.getValidator(
            idAndArgs[0],
            idAndArgs.subList(1, idAndArgs.size).toTypedArray()
        ) ?: throw RuntimeException("No field validator for ID ${idAndArgs[0]}.")
    }

    fun setOrAppendValidator(validator: FieldValidator) {
        this.validator = this.validator?.map {
            it.and(validator)
        } ?: validator
    }

    private fun contentsUpdated(text: String) {
        if (text.isEmpty()) {
            lastText = ""
            updateViewWithoutError()
            return
        }
        if (text == lastText) {
            return
        }
        lastText = text
        val task = createValidateTask(text)
        validationTaskExecutor.cancelAll()
        validationTaskExecutor.submit(task)
        task.setOnSucceeded {
            task.get()?.also { error ->
                Platform.runLater {
                    updateViewWithError(error)
                }
            } ?: Platform.runLater { updateViewWithoutError() }
        }
    }

    private fun updateViewWithError(error: String) {
        field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, true)
        errorLabel.text = error
    }

    private fun updateViewWithoutError() {
        field.pseudoClassStateChanged(ERROR_PSEUDO_CLASS, false)
        errorLabel.text = ""
    }

    private fun createValidateTask(text: String): Task<String?> {
        return object : Task<String?>() {
            override fun call(): String? {
                return try {
                    validator?.validate(text); null
                } catch (e: FieldValidator.InvalidException) {
                    e.message
                }
            }
        }
    }
}
