package br.com.guiabolso.hyperloop.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode

@JsonDeserialize(using = CustomKeyDeserializer::class)
//@JsonDeserialize(contentUsing = CustomKeyDeserializer::class)
sealed class Key {
        abstract val type: String
        abstract val required: Boolean
        abstract val encrypted: Boolean
}

data class BaseKey(
        override val type: String,
        override val required: Boolean,
        override val encrypted: Boolean
) : Key()

data class DateKey(
        override val type: String,
        override val required: Boolean,
        override val encrypted: Boolean,
        val format: String
) : Key()

data class Identity (
        val userId: BaseKey
)

data class Metadata (
        val origin: BaseKey
)


data class Validation(
        val payload: Map<String,Key>,
        val identity: Identity?,
        val metadata: Metadata?
)
//data class Schema(
//        val event_name: String,
//        val event_version: Int,
//        val validation: Validation
//)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Schema(
        val event_name: String,
        val event_version: Int,
        val types: ObjectNode,
        val validation: ObjectNode
)