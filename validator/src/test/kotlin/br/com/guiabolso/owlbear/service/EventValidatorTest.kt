package br.com.guiabolso.owlbear.service

import br.com.guiabolso.events.model.RequestEvent
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.junit.Before
import org.junit.Test

class EventValidatorTest {

    private lateinit var eventValidator: EventValidator

    @Before
    fun setUp() {
        eventValidator = EventValidator()
    }

    @Test
    fun shouldLoadYaml() {
        eventValidator.Validate(getRequestEvent())
    }

    private fun getRequestEvent() : RequestEvent {
        val identity = JsonObject()
        identity.addProperty("userId", "1")

        return RequestEvent(
                "send:sms",
                1,
                "Id",
                "flowId",
                JsonParser().parse("{\"userId\":1,\"phone\":\"1199346789\",\"token\":\"987654\"}"),
                identity,
                JsonObject(),
                JsonObject()
        )
    }
}