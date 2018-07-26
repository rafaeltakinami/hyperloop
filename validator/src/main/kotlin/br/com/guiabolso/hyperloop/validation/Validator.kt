package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.RequestEvent

interface Validator {

    fun validate(event: RequestEvent): ValidationResult
}

data class ValidationResult (
        val validationSuccess: Boolean,
        val validationErrors: List<Throwable>,
        val encryptedFields: List<String>
)