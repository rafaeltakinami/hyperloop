package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.hyperloop.cryptography.CryptographyEngine
import br.com.guiabolso.hyperloop.exceptions.SendMessageException
import br.com.guiabolso.hyperloop.transport.MessageResult
import br.com.guiabolso.hyperloop.transport.Transport
import br.com.guiabolso.hyperloop.validation.MockValidator
import br.com.guiabolso.hyperloop.validation.Validator
import com.google.gson.GsonBuilder

class Hyperloop(
        private val transport: Transport,
        private val cryptographyEngine: CryptographyEngine
) {

    private val validator: Validator = MockValidator()
    private val gson = GsonBuilder().serializeNulls().create()

    fun offer(event: Event): MessageResult {
        validator.validate(event)

        val encryptedData = cryptographyEngine.encrypt(gson.toJson(event))

        val encodedEvent = encryptedData.data.b64()

        val messageResult = transport.sendMessage(encodedEvent)

        if (messageResult.messageMD5 != encodedEvent.md5()) {
            throw SendMessageException("Transport could not deliver message correctly! MD5 from event differs from MD5 of transport.")
        }
        return messageResult
    }

}