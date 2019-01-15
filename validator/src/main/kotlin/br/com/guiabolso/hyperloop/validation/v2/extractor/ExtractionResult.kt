package br.com.guiabolso.hyperloop.validation.v2.extractor

import br.com.guiabolso.hyperloop.validation.v2.JsonPath
import br.com.guiabolso.hyperloop.validation.v2.extractor.json.JsonArrayExtractor
import br.com.guiabolso.hyperloop.validation.v2.extractor.json.JsonObjectExtractor
import br.com.guiabolso.hyperloop.validation.v2.extractor.json.JsonPrimitiveExtractor
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive

typealias ExtractionResult = Map<String, JsonPath>

fun MutableMap<String, JsonPath>.addElement(
    element: JsonElement,
    elementPath: String
) {
    when (element) {
        is JsonPrimitive, is JsonNull -> this.putAll(JsonPrimitiveExtractor.extract(element, elementPath))
        is JsonObject -> this.putAll(JsonObjectExtractor.extract(element, elementPath))
        is JsonArray -> this.putAll(JsonArrayExtractor.extract(element, elementPath))
    }
}