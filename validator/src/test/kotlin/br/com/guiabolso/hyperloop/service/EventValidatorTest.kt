package br.com.guiabolso.hyperloop.service

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.exceptions.ValidationException
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
        eventValidator.validate(newEvent("event_test", 1))
    }

    @Test(expected = ValidationException::class)
    fun `test with event name different from schema`() {
        eventValidator.validate(newEvent("xpto", 1))
    }


    @Test(expected = ValidationException::class)
    fun `test with event version different from schema`() {
        eventValidator.validate(newEvent("event_test", 3))
    }

    fun newEvent(eventName: String, eventVersion: Int): Event {
        val identity = JsonObject()
        identity.addProperty("userId", "1")

        return RequestEvent(
                eventName,
                eventVersion,
                "Id",
                "flowId",
                JsonParser().parse("""{
                    "name": "event:xpto",
                    "version": 1,
                    "payload": {
                        "file": {
                            "name": "document.png",
                            "owner": {
                                "name": "Thiago",
                                "birthdate": "22/05/1990",
                                "gender": "male"
                            }
                        }
                    }
                    }
                """.trimIndent()),
                identity,
                JsonObject(),
                JsonObject()
        )
    }
}