package br.com.guiabolso.hyperloop

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.cryptography.cypher.MessageCypher
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.exceptions.SendMessageException
import br.com.guiabolso.hyperloop.transport.MessageResult
import br.com.guiabolso.hyperloop.transport.Transport
import br.com.guiabolso.hyperloop.validation.ValidationResult
import br.com.guiabolso.hyperloop.validation.Validator
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import java.util.UUID

class HyperloopTest {

    private lateinit var hyperloop: Hyperloop
    private lateinit var transport: Transport
    private lateinit var validator: Validator
    private lateinit var cryptographyEngine: MessageCypher
    private lateinit var event: RequestEvent
    private lateinit var eventMD5: String
    private val gson = Gson()

    @Before
    fun setUp() {
        transport = mock()
        cryptographyEngine = mock()
        validator = mock()
        hyperloop = Hyperloop(transport, validator, cryptographyEngine)

        event = EventBuilder.event {
            name = "test:event"
            version = 1
            id = UUID.randomUUID().toString()
            flowId = UUID.randomUUID().toString()
            payload = jsonObject("answer" to 42)
            auth = jsonObject()
            metadata = jsonObject("origin" to "Kyoto - Japan")
        }

        eventMD5 = gson.toJson(event).md5()
    }

    @Test
    fun `test can send event as message`() {
        whenever(transport.sendMessage(any())).thenReturn(MessageResult("some-id", eventMD5))
        whenever(validator.validate(any())).thenReturn(ValidationResult(true, mutableListOf(), mutableListOf()))
        whenever(cryptographyEngine.cypher(any())).thenAnswer {
            it.arguments[0] as String
        }

        hyperloop.offer(event)

        verify(transport).sendMessage(gson.toJson(event))
    }


    @Test(expected = SendMessageException::class)
    fun `test send event fails with invalid md5`() {
        whenever(transport.sendMessage(any())).thenReturn(MessageResult("some-id", "wrong-md5-hash"))
        whenever(validator.validate(any())).thenReturn(ValidationResult(true, mutableListOf(), mutableListOf()))
        whenever(cryptographyEngine.cypher(any())).thenAnswer {
            it.arguments[0] as String
        }

        hyperloop.offer(event)
    }

    @Test(expected = ValidationException::class)
    fun `test validate event fails`() {
        whenever(transport.sendMessage(any())).thenReturn(MessageResult("some-id", "wrong-md5-hash"))
        whenever(validator.validate(any())).thenReturn(ValidationResult(false, mutableListOf(InvalidInputException("some input was invalid")), mutableListOf()))
        whenever(cryptographyEngine.cypher(any())).thenAnswer {
            it.arguments[0] as String
        }

        hyperloop.offer(event)
    }

}