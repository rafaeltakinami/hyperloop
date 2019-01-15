package br.com.guiabolso.hyperloop.validation.v2.extractor

import com.google.gson.JsonElement

interface PathExtractor<in T : JsonElement> {
    fun extract(element: T, initialPath: String): ExtractionResult
}

