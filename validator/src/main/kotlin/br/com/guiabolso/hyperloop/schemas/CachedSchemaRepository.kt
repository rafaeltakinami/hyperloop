package br.com.guiabolso.hyperloop.schemas

import br.com.guiabolso.hyperloop.schemas.exceptions.SchemaFetchingException
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

class CachedSchemaRepository(
        private val schemaRepository: SchemaRepository,
        maximumCacheSize: Long = 200L,
        expirationInMinutes: Long = 60L
) : SchemaRepository {

    private val schemaCache = Caffeine.newBuilder()
            .maximumSize(maximumCacheSize)
            .expireAfterWrite(expirationInMinutes, TimeUnit.MINUTES)
            .build<SchemaKey, String> { key ->
                retrieveSchema(key)
            }

    override fun get(schemaKey: SchemaKey) = schemaCache[schemaKey]
            ?: throw SchemaFetchingException("Could not fetch schema fot key $schemaKey")


    private fun retrieveSchema(key: SchemaKey) = try {
        schemaRepository.get(key)
    } catch (e: SchemaFetchingException) {
        null
    }
}