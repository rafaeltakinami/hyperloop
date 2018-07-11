package br.com.guiabolso.owlbear

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.owlbear.exceptions.ValidationException

interface Validator {

    @Throws(ValidationException::class)
    fun Validate(event: Event)
}