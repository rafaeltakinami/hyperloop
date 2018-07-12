package br.com.guiabolso.schemas

interface SchemaRepository {

    fun get(schemaKey : SchemaKey) : String
}