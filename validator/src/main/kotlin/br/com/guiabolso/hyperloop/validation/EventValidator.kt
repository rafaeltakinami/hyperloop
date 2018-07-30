package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.exceptions.WrongSchemaFormatException
import br.com.guiabolso.hyperloop.model.SchemaData
import br.com.guiabolso.hyperloop.schemas.CachedSchemaRepository
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.validation.types.ArrayType
import br.com.guiabolso.hyperloop.validation.types.DateType
import br.com.guiabolso.hyperloop.validation.types.PrimitiveType
import br.com.guiabolso.hyperloop.validation.types.SchemaNodeTypeParser
import br.com.guiabolso.hyperloop.validation.types.SchemaType
import br.com.guiabolso.hyperloop.validation.types.UserDefinedType
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
    private var validationSuccess = false
    private val validationErrors = mutableListOf<Throwable>()
    private val encryptedFields = mutableListOf<String>()
    private val elementPath = mutableListOf<String>()

    override fun validate(event: RequestEvent): ValidationResult {
        val schemaKey = SchemaKey(event.name, event.version)
        val schemaData = cachedSchemaRepository.get(schemaKey)

        if (schemaData.event.name != event.name)
            validationErrors.add(InvalidInputException("The event name '${event.name}' is different from schema '${schemaData.event.name}'"))
        if (schemaData.event.version != event.version)
            validationErrors.add(InvalidInputException("The event version '${event.version}' is different from schema '${schemaData.event.version}'"))
        val schemaPayloadSpec = schemaData.validation["payload"]?.fields()

        if (schemaPayloadSpec == null)
            validationErrors.add(WrongSchemaFormatException("The schema '$schemaKey' has no payload"))
        else {
            val eventPayloadContent = event.payload

            iterateSchemaElements(schemaPayloadSpec, eventPayloadContent, schemaData)

            schemaData.validation["identity"]?.fields()?.let { schemaIdentitySpec ->
                val eventIdentityContent = event.identity
                eventIdentityContent.asJsonObject["userId"]
                        ?: throw WrongSchemaFormatException("The event '${event.name}' has no userId")
                iterateSchemaElements(schemaIdentitySpec, eventIdentityContent, schemaData)
            }

            schemaData.validation["metadata"]?.fields()?.let { schemaMetadataSpec ->
                val eventMetadataContent = event.metadata
                eventMetadataContent.asJsonObject["origin"]
                        ?: throw WrongSchemaFormatException("The event '${event.name}' has no origin")
                iterateSchemaElements(schemaMetadataSpec, eventMetadataContent, schemaData)
            }
        }

        if (validationErrors.isEmpty()) validationSuccess = true
        return ValidationResult(validationSuccess, validationErrors, encryptedFields.distinct())
    }

    private fun iterateSchemaElements(
            schemaNodeSpec: InputSchemaSpec,
            eventNode: JsonElement,
            schemaData: SchemaData
    ) {
        schemaNodeSpec.forEach { (key, node) ->
            val eventNodeElement = eventNode.asJsonObject[key]
//            if (eventNodeElement != null && !eventNodeElement.isJsonNull && this.isEncrypted(node)) {
//                elementPath.add(key)
//                encryptedFields.add(elementPath.toString())
//                elementPath.remove(elementPath.lastOrNull())
//            }
            try {
                val expectedType = SchemaNodeTypeParser.getSchemaNodeType(schemaData, key, node)
                this.validateRequiredElement(key, node, eventNodeElement)
                eventNodeElement?.let { this.validateByType(node, key, expectedType, false, eventNodeElement, schemaData) }
            }
            catch (exception: Exception) {
                validationErrors.add(exception)
            }
        }
    }

    private fun isEncrypted(specNode: JsonNode): Boolean {
        val schemaNodeAttributes = getSchemaNodeAttributes(specNode) ?: return false
        return TextNode("encrypted") in schemaNodeAttributes
    }

    private fun validateByType(
            eventNodeSpec: JsonNode,
            nodeKey: String,
            type: SchemaType,
            isArrayElement: Boolean,
            inputNode: JsonElement,
            schemaData: SchemaData
    ) {
        try {
            if (!inputNode.isJsonNull && this.isEncrypted(eventNodeSpec)) {
                elementPath.add(nodeKey)
                encryptedFields.add(elementPath.toString())
                elementPath.remove(elementPath.lastOrNull())
            }
            when (type) {

                is ArrayType -> {
                    this.validateArrayElement(eventNodeSpec, nodeKey, type, inputNode, schemaData)
                    elementPath.remove(elementPath.lastOrNull())
                }
                is DateType -> this.validateDateElement(type, inputNode)
                is UserDefinedType -> {
                    if (isArrayElement)
                        elementPath.add("$nodeKey[*]")
                    else
                        elementPath.add(nodeKey)

                    val currentTypeSpec = type.userType.fields()
                    iterateSchemaElements(currentTypeSpec, inputNode, schemaData)
                    elementPath.remove(elementPath.lastOrNull())
                }
                is PrimitiveType -> type.type.verifyType(inputNode.asJsonPrimitive)
            }
        }
        catch (exception: Exception) {
            validationErrors.add(exception)
        }
    }

    private fun validateRequiredElement(
            nodeKey: String,
            specNode: JsonNode,
            inputElement: JsonElement?
    ) {
        if (isRequired(specNode) && (inputElement == null || inputElement.isJsonNull))
            throw InvalidInputException("Element '$nodeKey' is required")
    }

    private fun isRequired(specNode: JsonNode): Boolean {
        val schemaNodeAttributes = getSchemaNodeAttributes(specNode) ?: return false
        return TextNode("required") in schemaNodeAttributes
    }

    private fun validateArrayElement(
            eventNodeSpec: JsonNode,
            nodeKey: String,
            type: ArrayType,
            arrayElement: JsonElement,
            schemaData: SchemaData
    ) {
        if (!arrayElement.isJsonArray) {
            throw InvalidInputException("Array element '${type.nodeKey}' is in the wrong format")
        }
        arrayElement.asJsonArray.forEach {
            validateByType(eventNodeSpec, nodeKey, type.contentType, true, it, schemaData)
        }
    }

    private fun validateDateElement(type: DateType, dateElement: JsonElement) {
        val simpleDateFormat = SimpleDateFormat(type.format)
        try {
            simpleDateFormat.parse(dateElement.asString)
        } catch (e: ParseException) {
            throw InvalidInputException("Date Element '${dateElement.asString}' is not in the format '${type.format}'")
        }
    }

    private fun getSchemaNodeAttributes(specNode: JsonNode): JsonNode? {
        return specNode.get("is")
    }
}
