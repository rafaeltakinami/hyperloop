package br.com.guiabolso.schemas

data class SchemaKey(val name: String,
                     val version: Int) {

    companion object Builder {
        fun parse(key: String): SchemaKey {
            return Regex.PARSE_WORKFLOW_KEY.find(key)?.let { match ->
                SchemaKey(match.groupValues[2], match.groupValues[3].toInt())
            } ?: throw IllegalArgumentException("Key: $key format not supported.")
        }
    }

    override fun toString(): String {
        return "${name}_V$version.json"
    }

}