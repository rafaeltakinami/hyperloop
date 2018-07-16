package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.hyperloop.exceptions.ValidationException

interface Validator {

    @Throws(ValidationException::class)
    fun validate(event: Event)
}