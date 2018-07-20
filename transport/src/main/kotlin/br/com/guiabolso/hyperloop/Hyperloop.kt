package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.hyperloop.cryptography.cypher.MessageCypher
import br.com.guiabolso.hyperloop.exceptions.SendMessageException
import br.com.guiabolso.hyperloop.transport.MessageResult
import br.com.guiabolso.hyperloop.transport.Transport
import br.com.guiabolso.hyperloop.validation.MockValidator
import br.com.guiabolso.hyperloop.validation.Validator
import com.google.gson.GsonBuilder

class Hyperloop(
        private val transport: Transport,
        private val messageCypher: MessageCypher
) {

    private val validator: Validator = MockValidator()
    private val gson = GsonBuilder().serializeNulls().create()

    fun offer(event: Event): MessageResult {
        validator.validate(event)

        val encryptedData = messageCypher.cypher(gson.toJson(event))

        val messageResult = transport.sendMessage(encryptedData)

        if (messageResult.messageMD5 != encryptedData.md5()) {
            throw SendMessageException("Transport could not deliver message correctly! MD5 from event differs from MD5 of transport.")
        }
        return messageResult
    }

}