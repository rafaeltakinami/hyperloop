package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.RequestEvent

interface Validator {

    fun validate(event: RequestEvent): ValidationResult
}

data class ValidationResult (
        var validationSuccess: Boolean,
        val validationErrors: MutableSet<Throwable>,
        val encryptedFields: MutableSet<String>
)