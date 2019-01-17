package br.com.guiabolso.hyperloop.validation

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.schemas.CachedSchemaRepository
import br.com.guiabolso.hyperloop.schemas.SchemaDataRepository
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.validation.v1.EventValidator
import br.com.guiabolso.hyperloop.validation.v2.EventValidatorV2
import br.com.guiabolso.hyperloop.validation.v2.parser.tree.SchemaTreeRepository

class VersionedEventValidator(schemaRepository: SchemaRepository<String>) : Validator {
    private val cachedSchemaDataRepository = CachedSchemaRepository(SchemaDataRepository(schemaRepository))
    private val eventValidator: EventValidator
    private val eventValidatorV2: EventValidatorV2

    init {
        eventValidator = EventValidator(cachedSchemaDataRepository)
        eventValidatorV2 = EventValidatorV2(CachedSchemaRepository(SchemaTreeRepository(schemaRepository)))
    }


    override fun validate(event: RequestEvent): ValidationResult {
        val schemaData = cachedSchemaDataRepository.get(SchemaKey(event.name, event.version))
        return when (schemaData.schema.version) {
            1 -> eventValidator.validate(event)
            2 -> eventValidatorV2.validate(event)
            else -> throw IllegalArgumentException("Schema with invalid version: ${schemaData.schema.version}")
        }

    }
}