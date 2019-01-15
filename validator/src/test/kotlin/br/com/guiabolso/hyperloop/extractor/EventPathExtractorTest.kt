package br.com.guiabolso.hyperloop.extractor

import br.com.guiabolso.events.builder.EventBuilder
import br.com.guiabolso.hyperloop.validation.v2.extractor.event.EventPathExtractor
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.junit.Assert.assertNotNull
import org.junit.Test

class EventPathExtractorTest {


    @Test
    fun `can extract json path from event`() {
        val event = EventBuilder.event {
            name = "test"
            version = 1
            flowId = "flow-id"
            id = "id"
            payload = JsonObject().apply {
                addProperty("answer", 42)
                add("nested", JsonObject().apply { addProperty("someStr", "wow such code") })
                add("array",
                    JsonArray().apply {
                        add(JsonObject().apply { addProperty("anotherStr", "wow such string") })
                        add(JsonObject().apply { addProperty("yetAnotherStr", "wow another string") })
                    })
            }
            metadata = JsonObject().apply {
                addProperty("origin", "some strange system")
            }
        }

        val jsonPaths = EventPathExtractor.extract(event)

        assertNotNull(jsonPaths)
    }

}

