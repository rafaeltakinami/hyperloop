package br.com.guiabolso.hyperloop.service

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
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
    fun `test succesfull validation`() {
        val payload = """
                        "users": [
                            {
                                "name": "Bruno",
                                "birthdate": "01/01/1990",
                                "gender": "male",
                                "id": 1111111111,
                                "married": false,
                                "height": 2.111,
                                "age": 28,
                                "friend":
                                {
                                    "name": "Carlos",
                                    "birthdate": "22/05/1990",
                                    "gender": "male",
                                    "id": 222222222,
                                    "married": true,
                                    "height": 3.111,
                                    "age": 28
                                }
                            },
                            {
                                "name": "Carlos",
                                "birthdate": "22/05/1990",
                                "gender": "male",
                                "id": 222222222,
                                "married": true,
                                "height": 3.111,
                                "age": 28
                            }
                        ],
                        "file": {
                            "name": "file",
                            "size": 123456.012456398725,
                            "quantity": 1233456877895613,
                            "owner": [
                                {
                                    "name": "Bruno",
                                    "birthdate": "01/01/1990",
                                    "gender": "male",
                                    "id": 1111111111,
                                    "married": false,
                                    "height": 2.111,
                                    "age": 28
                                },
                                {
                                    "name": "Carlos",
                                    "birthdate": "22/05/1990",
                                    "gender": "male",
                                    "id": 222222222,
                                    "married": true,
                                    "height": 3.111,
                                    "age": 28
                                }
                            ]
                        },
                        "name": "Thiago",
                        "x": "Thiago",
                        "y": "Thiago",
                        "o": "Thiago",
                        "k": "Thiago",
                        "l": "Thiago"
                """.trimIndent()
        eventValidator.validate(newEvent("event_test", 1, payload))
    }

    @Test(expected = InvalidInputException::class)
    fun `test wrong array type validation`() {
        val payload = """
                        "users": "notAnArray"
                """.trimIndent()
        eventValidator.validate(newEvent("event_test", 1, payload))
    }

    @Test(expected = InvalidInputException::class)
    fun `test missing required field`() {
        eventValidator.validate(newEvent("event_test", 1, ""))
    }

    @Test(expected = InvalidInputException::class)
    fun `test with event name different from schema`() {
        eventValidator.validate(newEvent("xpto", 1, ""))
    }


    @Test(expected = InvalidInputException::class)
    fun `test with event version different from schema`() {
        eventValidator.validate(newEvent("event_test", 3, ""))
    }

    fun newEvent(eventName: String, eventVersion: Int, payload: String): Event {
        val identity = JsonObject()
        identity.addProperty("userId", 1)

        val metadata = JsonObject()
        metadata.addProperty("origin", "origin")

        return RequestEvent(
                eventName,
                eventVersion,
                "Id",
                "flowId",
                JsonParser().parse("""{
                        $payload
                }"""),
                identity,
                JsonObject(),
                metadata
        )
    }
}