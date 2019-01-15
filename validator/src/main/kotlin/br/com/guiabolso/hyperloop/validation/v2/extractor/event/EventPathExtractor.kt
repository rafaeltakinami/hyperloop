package br.com.guiabolso.hyperloop.validation.v2.extractor.event

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.validation.v2.JsonPath
import br.com.guiabolso.hyperloop.validation.v2.extractor.PathExtractor
import br.com.guiabolso.hyperloop.validation.v2.extractor.json.JsonArrayExtractor
import br.com.guiabolso.hyperloop.validation.v2.extractor.json.JsonObjectExtractor
import br.com.guiabolso.hyperloop.validation.v2.extractor.json.JsonPrimitiveExtractor
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

object EventPathExtractor {

    @Suppress("UNCHECKED_CAST")
    private val extractors = mapOf(
        JsonObject::class.java to JsonObjectExtractor as PathExtractor<JsonElement>,
        JsonArray::class.java to JsonArrayExtractor as PathExtractor<JsonElement>,
        JsonPrimitive::class.java to JsonPrimitiveExtractor,
        JsonNull::class.java to JsonPrimitiveExtractor
    )

    fun extract(event: RequestEvent): Map<String, JsonPath> {
        val result = mutableMapOf<String, JsonPath>()

        result.putAll(extractors[event.payload::class.java]!!.extract(event.payload, "$.payload"))
        result.putAll(extractors[JsonObject::class.java]!!.extract(event.identity, "$.identity"))
        result.putAll(extractors[JsonObject::class.java]!!.extract(event.auth, "$.auth"))
        result.putAll(extractors[JsonObject::class.java]!!.extract(event.metadata, "$.metadata"))

        return result
    }
}
