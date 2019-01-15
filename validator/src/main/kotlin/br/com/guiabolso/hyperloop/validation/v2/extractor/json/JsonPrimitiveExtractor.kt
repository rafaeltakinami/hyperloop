package br.com.guiabolso.hyperloop.validation.v2.extractor.json

import br.com.guiabolso.hyperloop.validation.v2.JsonPath
import br.com.guiabolso.hyperloop.validation.v2.extractor.ExtractionResult
import br.com.guiabolso.hyperloop.validation.v2.extractor.PathExtractor
import com.google.gson.JsonElement
import java.util.Collections.singleton

object JsonPrimitiveExtractor : PathExtractor<JsonElement> {
    override fun extract(element: JsonElement, initialPath: String): ExtractionResult {
        return mapOf(initialPath to JsonPath(initialPath, element))
    }
}