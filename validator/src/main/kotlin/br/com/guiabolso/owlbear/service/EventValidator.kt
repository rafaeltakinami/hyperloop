package br.com.guiabolso.owlbear.service

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.owlbear.Validator
import br.com.guiabolso.owlbear.model.Schema
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.yaml.snakeyaml.Yaml
import java.io.File

@JsonIgnoreProperties(ignoreUnknown = true)
class EventValidator : Validator {

    private val schemaPath = "src/main/resources/"

    // private val schemaNoPayloadStr = this.loadFromFile(schemaPath + "schema_noPayload.yml")
    //    private val schemaStr = this.getFile(schemaPath + "schema.yml")
    private val schemaStr = this.loadFromFile(schemaPath + "schema.yml")


    override fun Validate(event: RequestEvent) {
        val yaml =  Yaml()

        //val schemaYaml = yaml.loadAs(schemaStr, Schema::class.java)

        TODO("check null")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getFile(path: String): String{
        return File(path).readText()
    }

    fun loadFromFile(path: String): Schema {
        val mapper = ObjectMapper(YAMLFactory())
                .registerModule(KotlinModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                //.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true)

//        return Files.newBufferedReader(path).use {
        return File(path).bufferedReader().use {
            mapper.readValue(it, Schema::class.java)
        }
    }


}