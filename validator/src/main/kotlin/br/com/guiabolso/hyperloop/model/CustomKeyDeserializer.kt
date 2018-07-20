package br.com.guiabolso.hyperloop.model

import br.com.guiabolso.hyperloop.exceptions.ValidationException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode

//class CustomKeyDeserializer : StdDeserializer<Key>(Key::class.java) {
////    override fun deserialize(jp: JsonParser, ctxt: DeserializationContext?): Key {
////        val keyNode: JsonNode = jp.codec.readTree(jp)
////        val required = keyNode.get("required")?.asBoolean() ?: throw ValidationException("Field 'required' cannot be null")
////        val encrypted = keyNode.get("encrypted")?.asBoolean() ?: false
////        val format = keyNode.get("format")?.textValue()
////        val type = keyNode.get("type")?.textValue() ?: throw ValidationException("Field 'type' cannot be null")
////
////        return when (type){
////            "date" -> {
////                format ?: throw ValidationException("Field 'format' cannot be null")
////                DateKey(type, required, encrypted, format)
////            }
////            else -> BaseKey(type, required, encrypted)
////        }
////    }
//}