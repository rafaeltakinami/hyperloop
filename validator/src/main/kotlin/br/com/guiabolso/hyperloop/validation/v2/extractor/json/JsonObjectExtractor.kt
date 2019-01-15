package br.com.guiabolso.hyperloop.validation.v2.extractor.json

import br.com.guiabolso.hyperloop.validation.v2.JsonPath
import br.com.guiabolso.hyperloop.validation.v2.extractor.ExtractionResult
import br.com.guiabolso.hyperloop.validation.v2.extractor.PathExtractor
import br.com.guiabolso.hyperloop.validation.v2.extractor.addElement
import com.google.gson.JsonObject

object JsonObjectExtractor : PathExtractor<JsonObject> {
    override fun extract(element: JsonObject, initialPath: String): ExtractionResult {
        val result = mutableMapOf<String, JsonPath>()
        for ((key, el) in element.entrySet()) {
            result.addElement(el, "$initialPath.$key")
        }
        return result
    }
}