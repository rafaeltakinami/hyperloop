package br.com.guiabolso.hyperloop.validation.types

import br.com.guiabolso.hyperloop.exceptions.WrongSchemaFormatException
import br.com.guiabolso.hyperloop.model.SchemaData
import com.fasterxml.jackson.databind.JsonNode

object SchemaNodeTypeParser {

    private val typeRegex = "^(\\\$?\\w+)(\\((.+)\\))?".toRegex(RegexOption.IGNORE_CASE)

    fun getSchemaNodeType(schema: SchemaData, nodeKey: String, specNode: JsonNode): SchemaType {
        val rawType = specNode.get("of")?.asText() ?: throw WrongSchemaFormatException("Missing type for key $nodeKey")
        val groups = typeRegex.find(rawType)?.groupValues
                ?: throw WrongSchemaFormatException("Illegal type $rawType. $rawType is neither primitive, array, date nor user defined")
        val type = groups[1]
        val attribute = if (groups.size > 1) {
            groups.last()
        } else null
        return when (type.toLowerCase()) {
            "string", "long", "int", "float", "double", "boolean" -> PrimitiveType(nodeKey, type)
            "array" -> {
                val arrayContentType = when (attribute) {
                    "string", "long", "int", "float", "double", "boolean" -> PrimitiveType(nodeKey, type)
                    "date" -> DateType(nodeKey, attribute)
                    else -> if (isUserDefinedType(attribute.notNull("Array content type should not be null"), schema)) {
                        UserDefinedType(nodeKey, schema.types!!.get(attribute))
                    } else {
                        throw WrongSchemaFormatException("Illegal type $type. $type is neither primitive, array, date nor user defined")
                    }
                }
                ArrayType(nodeKey, arrayContentType)
            }
            "date" -> DateType(nodeKey, attribute.notNull("Date type should have one parameter"))
            else -> if (isUserDefinedType(type, schema)) {
                UserDefinedType(nodeKey, schema.types!!.get(type))
            } else {
                throw WrongSchemaFormatException("Illegal type $type. $type is neither primitive, array, date nor user defined")
            }
        }
    }

    private fun isUserDefinedType(type: String, schema: SchemaData): Boolean {
        return schema.types?.get(type) != null
    }

    private fun String?.notNull(message: String) = this ?: throw WrongSchemaFormatException(message)
}