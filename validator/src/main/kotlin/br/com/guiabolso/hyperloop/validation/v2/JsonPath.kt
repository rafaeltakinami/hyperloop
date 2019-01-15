package br.com.guiabolso.hyperloop.validation.v2

import com.google.gson.JsonElement

data class JsonPath(
    val path: String,
    val value: JsonElement
)