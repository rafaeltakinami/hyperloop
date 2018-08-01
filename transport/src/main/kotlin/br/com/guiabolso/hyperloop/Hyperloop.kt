package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.cryptography.cypher.MessageCypher
import br.com.guiabolso.hyperloop.cryptography.cypher.NoOpMessageCypher
import br.com.guiabolso.hyperloop.exceptions.SendMessageException
import br.com.guiabolso.hyperloop.transport.MessageResult
import br.com.guiabolso.hyperloop.transport.Transport
import br.com.guiabolso.hyperloop.validation.Validator
import com.google.gson.GsonBuilder

class Hyperloop
@JvmOverloads
constructor(
        private val transport: Transport,
        private val validator: Validator,
        private val messageCypher: MessageCypher = NoOpMessageCypher
) {

    private val gson = GsonBuilder().serializeNulls().create()

    fun offer(event: RequestEvent): MessageResult {
        validator.validate(event)

        val encryptedData = messageCypher.cypher(gson.toJson(event))

        val messageResult = transport.sendMessage(encryptedData)

        if (messageResult.messageMD5 != encryptedData.md5()) {
            throw SendMessageException("Transport could not deliver message correctly! MD5 from event differs from MD5 of transport.")
        }
        return messageResult
    }

}