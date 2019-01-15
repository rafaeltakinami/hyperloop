package br.com.guiabolso.hyperloop.validation.v2.extractor.json

import br.com.guiabolso.hyperloop.validation.v2.JsonPath
import br.com.guiabolso.hyperloop.validation.v2.extractor.ExtractionResult
import br.com.guiabolso.hyperloop.validation.v2.extractor.PathExtractor
import br.com.guiabolso.hyperloop.validation.v2.extractor.addElement
import com.google.gson.JsonArray

object JsonArrayExtractor : PathExtractor<JsonArray> {

    override fun extract(element: JsonArray, initialPath: String): ExtractionResult {
        val result = mutableMapOf<String, JsonPath>()
        for ((idx, el) in element.withIndex()) {
            result.addElement(el, "$initialPath[$idx]")
        }
        return result
    }

}