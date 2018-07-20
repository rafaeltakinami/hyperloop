package br.com.guiabolso.hyperloop.validation.service

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.hyperloop.validation.Validator
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.exceptions.SchemaWrongFormatException
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import br.com.guiabolso.hyperloop.model.SchemaData
import br.com.guiabolso.hyperloop.schemas.CachedSchemaRepository
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.utils.verifyBoolean
import br.com.guiabolso.hyperloop.utils.verifyDouble
import br.com.guiabolso.hyperloop.utils.verifyFloat
import br.com.guiabolso.hyperloop.utils.verifyInt
import br.com.guiabolso.hyperloop.utils.verifyLong
import br.com.guiabolso.hyperloop.utils.verifyString
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.google.gson.JsonElement
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.collections.MutableMap.MutableEntry

typealias InputSchemaSpec = MutableIterator<MutableEntry<String, JsonNode>>

@JsonIgnoreProperties(ignoreUnknown = true)
class EventValidator(private val cachedSchemaRepository: CachedSchemaRepository<SchemaData>) : Validator {

    private val dateTypeRegex = "^(date)(\\((.*)\\))?".toRegex(RegexOption.IGNORE_CASE)
    private val arrayTypeRegex = "^(array)(\\((.*)\\))?".toRegex(RegexOption.IGNORE_CASE)

    override fun validate(event: Event) {
        val schemaData = cachedSchemaRepository.get(SchemaKey(event.name, event.version))

        if (schemaData.event.name != event.name)
            throw InvalidInputException("The event name ${event.name} is different from schema")
        if (schemaData.event.version != event.version)
            throw InvalidInputException("The event version ${event.version} is different from schema")

        //TODO(get schema name)
        val schemaPayloadSpec = schemaData.validation["payload"].fields()
                ?: throw SchemaWrongFormatException("The schema $schemaData has no payload")
        val eventPayloadContent = event.payload
        validateAllElements(schemaPayloadSpec, eventPayloadContent, schemaData)

        val schemaIdentitySpec = schemaData.validation["identity"].fields()
                ?: throw SchemaWrongFormatException("The schema $schemaData has no identity")
        val eventIdentityContent = event.identity
        eventIdentityContent.asJsonObject["userId"] ?: throw ValidationException("The event ${event.name} has no userId")
        validateAllElements(schemaIdentitySpec, eventIdentityContent, schemaData)

        val schemaMetadataSpec = schemaData.validation["metadata"].fields()
                ?: throw SchemaWrongFormatException("The schema $schemaData has no metadata")
        val eventMetadataContent = event.metadata
        eventMetadataContent.asJsonObject["origin"] ?: throw ValidationException("The event ${event.name} has no origin")
        validateAllElements(schemaMetadataSpec, eventMetadataContent, schemaData)
    }

    private fun validateAllElements(
            schemaPayloadSpec: InputSchemaSpec,
            eventPayloadContent: JsonElement,
            schemaData: SchemaData
    ) {
        schemaPayloadSpec.forEach { entry ->
            val eventPayloadNode = eventPayloadContent.asJsonObject[entry.key]
            val expectedType = getSchemaNodeType(entry)?.asText()
                    ?: throw SchemaWrongFormatException("The schema has a null type for element ${entry.key}")
            this.validateRequiredElement(entry, eventPayloadNode)
            eventPayloadNode?.let { this.validateByType(expectedType, eventPayloadNode, schemaData) }
        }
    }

    private fun validateByType(
            type: String,
            inputNode: JsonElement,
            schemaData: SchemaData) {
        when {
            isArrayType(type) -> this.validateArrayElement(type, inputNode, schemaData)
            isDateType(type) -> this.validateDateElement(type, inputNode)
            isUserDefinedType(type, schemaData) -> {
                val currentTypeSpec = schemaData.types.get(type)?.fields()
                        ?: throw SchemaWrongFormatException("The schema has an empty user defined type")
                validateAllElements(currentTypeSpec, inputNode, schemaData)
            }
            else -> this.validatePrimitiveElement(type, inputNode)
        }
    }

    private fun validateRequiredElement(
            specNode: MutableEntry<String, JsonNode>,
            inputElement: JsonElement?
    ) {
        if (isRequired(specNode) && (inputElement == null || inputElement.isJsonNull))
            throw InvalidInputException("Element '${specNode.key}' is required.")
    }

    private fun isRequired(specNode: MutableEntry<String, JsonNode>): Boolean {
        val schemaNodeAttributes = getSchemaNodeAttributes(specNode) ?: return false
        return TextNode("required") in schemaNodeAttributes
    }

    private fun validateArrayElement(
            type: String,
            arrayElement: JsonElement,
            schemaData: SchemaData
    ) {
        val arrayTypeSpec = arrayTypeRegex.find(type)?.groups?.last()?.value
                ?: throw InvalidInputException("Array element $type has no content type.")
        if (arrayElement.isJsonArray) {
            arrayElement.asJsonArray.forEach {
                validateByType(arrayTypeSpec, it, schemaData)
            }
        } else throw InvalidInputException("Array element $type is in the wrong format.")
    }

    private fun validateDateElement(type: String, dateElement: JsonElement) {
        val schemaDateFormat = dateTypeRegex.find(type)?.groups?.last()?.value
                ?: throw InvalidInputException("Date element has no format.")
        val simpleDateFormat = SimpleDateFormat(schemaDateFormat)
        try {
            simpleDateFormat.parse(dateElement.asString)
        } catch (e: ParseException) {
            throw InvalidInputException("Date Element ${dateElement.asString} is not in the format '$schemaDateFormat'.")
        }
    }

    private fun validatePrimitiveElement(type: String, primitiveElement: JsonElement) {
        when (type.toUpperCase().trim()) {
            "STRING" -> primitiveElement.verifyString()
            "LONG" -> primitiveElement.verifyLong()
            "INT" -> primitiveElement.verifyInt()
            "FLOAT" -> primitiveElement.verifyFloat()
            "DOUBLE" -> primitiveElement.verifyDouble()
            "BOOLEAN" -> primitiveElement.verifyBoolean()
            else -> throw ValidationException("Invalid schema primitive type: $String")
        }
    }

    private fun getSchemaNodeAttributes(specNode: MutableEntry<String, JsonNode>): JsonNode? {
        return specNode.value.get("is")
    }

    private fun getSchemaNodeType(specNode: MutableEntry<String, JsonNode>): JsonNode? {
        return specNode.value.get("of")
    }

    private fun isArrayType(type: String): Boolean {
        return arrayTypeRegex.matches(type)
    }

    private fun isDateType(type: String): Boolean {
        return dateTypeRegex.matches(type)
    }

    private fun isUserDefinedType(type: String, schema: SchemaData): Boolean {
        return schema.types.get(type) != null
    }
}
