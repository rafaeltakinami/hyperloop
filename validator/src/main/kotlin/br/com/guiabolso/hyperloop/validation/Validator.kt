package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException

interface Validator {

    @Throws(ValidationException::class)
    fun validate(event: Event)
}