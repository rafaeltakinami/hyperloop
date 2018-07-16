package br.com.guiabolso.hyperloop.service

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.Validator
import br.com.guiabolso.hyperloop.exceptions.ValidationException
import br.com.guiabolso.hyperloop.model.Schema
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
class EventValidator : Validator {

    private val schemaPath = "src/main/resources/"

    private val schemaStr = this.loadFromFile(schemaPath + "schema.yml")

    override fun validate(event: Event) {
        //Step 1 - Verify event_name and event_version
        if (schemaStr.event_name != event.name)
            throw ValidationException("The event name ${event.name} is different from schema")
        if (schemaStr.event_version != event.version)
            throw ValidationException("The event version ${event.version} is different from schema")

        //Step 2 - verify Validation
        val types = schemaStr.types
        val schemaValidationNode = schemaStr.validation
        schemaStr.validation.get("payload").elements().forEach {
            System.out.println("")
        }

        //Step 3 - verify identify and metadata
    }

    fun loadFromFile(path: String): Schema {
        val mapper = ObjectMapper(YAMLFactory())
                .registerModule(KotlinModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)

        return File(path).bufferedReader().use {
            mapper.readValue(it, Schema::class.java)
        }
    }


}
