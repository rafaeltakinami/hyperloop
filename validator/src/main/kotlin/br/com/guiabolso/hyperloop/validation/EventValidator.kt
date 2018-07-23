package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.Event
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.exceptions.WrongSchemaFormatException
import br.com.guiabolso.hyperloop.model.SchemaData
import br.com.guiabolso.hyperloop.schemas.CachedSchemaRepository
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.validation.exceptions.ValidationException
import br.com.guiabolso.hyperloop.validation.types.ArrayType
import br.com.guiabolso.hyperloop.validation.types.DateType
import br.com.guiabolso.hyperloop.validation.types.PrimitiveType
import br.com.guiabolso.hyperloop.validation.types.SchemaNodeTypeParser
import br.com.guiabolso.hyperloop.validation.types.SchemaType
import br.com.guiabolso.hyperloop.validation.types.UserDefinedType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode
import com.google.gson.JsonElement
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.collections.MutableMap.MutableEntry

typealias InputSchemaSpec = MutableIterator<MutableEntry<String, JsonNode>>

class EventValidator(
        private val cachedSchemaRepository: CachedSchemaRepository<SchemaData>
) : Validator {

    override fun validate(event: Event) {
        val schemaKey = SchemaKey(event.name, event.version)
        val schemaData = cachedSchemaRepository.get(schemaKey)

        if (schemaData.event.name != event.name)
            throw InvalidInputException("The event name ${event.name} is different from schema")
        if (schemaData.event.version != event.version)
            throw InvalidInputException("The event version ${event.version} is different from schema")

        val schemaPayloadSpec = schemaData.validation["payload"]?.fields()
                ?: throw WrongSchemaFormatException("The schema for '$schemaKey' has no payload")
        val eventPayloadContent = event.payload
        validateAllElements(schemaPayloadSpec, eventPayloadContent, schemaData)

        schemaData.validation["identity"]?.fields()?.let {schemaIdentitySpec ->
            val eventIdentityContent = event.identity
            eventIdentityContent.asJsonObject["userId"]
                    ?: throw ValidationException("The event ${event.name} has no userId")
            validateAllElements(schemaIdentitySpec, eventIdentityContent, schemaData)
        }

        schemaData.validation["metadata"]?.fields()?.let {schemaMetadataSpec ->
            val eventMetadataContent = event.metadata
            eventMetadataContent.asJsonObject["origin"]
                    ?: throw ValidationException("The event ${event.name} has no origin")
            validateAllElements(schemaMetadataSpec, eventMetadataContent, schemaData)
        }
    }

    private fun validateAllElements(
            schemaPayloadSpec: InputSchemaSpec,
            eventPayloadContent: JsonElement,
            schemaData: SchemaData
    ) {
        schemaPayloadSpec.forEach { (key, node) ->
            val eventPayloadNode = eventPayloadContent.asJsonObject[key]
            val expectedType = SchemaNodeTypeParser.getSchemaNodeType(schemaData, key, node)

            this.validateRequiredElement(key, node, eventPayloadNode)

            eventPayloadNode?.let { this.validateByType(expectedType, eventPayloadNode, schemaData) }
        }
    }

    private fun validateByType(
            type: SchemaType,
            inputNode: JsonElement,
            schemaData: SchemaData) {
        when (type) {
            is ArrayType -> this.validateArrayElement(type, inputNode, schemaData)
            is DateType -> this.validateDateElement(type, inputNode)
            is UserDefinedType -> {
                val currentTypeSpec = type.userType.fields()
                validateAllElements(currentTypeSpec, inputNode, schemaData)
            }
            is PrimitiveType -> type.type.verifyType(inputNode.asJsonPrimitive)
        }
    }

    private fun validateRequiredElement(
            nodeKey: String,
            specNode: JsonNode,
            inputElement: JsonElement?
    ) {
        if (isRequired(specNode) && (inputElement == null || inputElement.isJsonNull))
            throw InvalidInputException("Element '$nodeKey' is required.")
    }

    private fun isRequired(specNode: JsonNode): Boolean {
        val schemaNodeAttributes = getSchemaNodeAttributes(specNode) ?: return false
        return TextNode("required") in schemaNodeAttributes
    }

    private fun validateArrayElement(
            type: ArrayType,
            arrayElement: JsonElement,
            schemaData: SchemaData
    ) {
        if (!arrayElement.isJsonArray) {
            throw InvalidInputException("Array element ${type.nodeKey} is in the wrong format.")
        }
        arrayElement.asJsonArray.forEach {
            validateByType(type.contentType, it, schemaData)
        }
    }

    private fun validateDateElement(type: DateType, dateElement: JsonElement) {
        val simpleDateFormat = SimpleDateFormat(type.format)
        try {
            simpleDateFormat.parse(dateElement.asString)
        } catch (e: ParseException) {
            throw InvalidInputException("Date Element ${dateElement.asString} is not in the format '${type.format}'.")
        }
    }

    private fun getSchemaNodeAttributes(specNode: JsonNode): JsonNode? {
        return specNode.get("is")
    }
}
