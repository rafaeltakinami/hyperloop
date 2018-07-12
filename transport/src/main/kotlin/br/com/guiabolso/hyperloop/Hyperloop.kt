package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.hyperloop.exceptions.SendMessageException
import br.com.guiabolso.hyperloop.transport.Transport
import com.google.gson.GsonBuilder

class Hyperloop(
        private val transport: Transport
) {

    private val validator: Validator = MockValidator()
    private val gson = GsonBuilder().serializeNulls().create()

    fun offer(event: Event) {
        validator.validate(event)

        event.metadata["origin"] ?: throw IllegalArgumentException("Origin must be present on event metadata")

        val eventString = gson.toJson(event)

        val messageResult = transport.sendMessage(eventString)

        if (messageResult.messageMD5 != eventString.md5()) {
            throw SendMessageException("Transport could not deliver message correctly! MD5 from event differs from MD5 of transport.")
        }
    }

}