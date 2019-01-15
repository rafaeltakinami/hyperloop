package br.com.guiabolso.hyperloop.validation.v2

import br.com.guiabolso.events.model.RequestEvent
import br.com.guiabolso.hyperloop.exceptions.InvalidInputException
import br.com.guiabolso.hyperloop.schemas.CachedSchemaRepository
import br.com.guiabolso.hyperloop.schemas.SchemaKey
import br.com.guiabolso.hyperloop.schemas.SchemaRepository
import br.com.guiabolso.hyperloop.schemas.parser.tree.SchemaTree
import br.com.guiabolso.hyperloop.schemas.parser.tree.SchemaTreeParser
import br.com.guiabolso.hyperloop.validation.ValidationResult
import br.com.guiabolso.hyperloop.validation.Validator
import br.com.guiabolso.hyperloop.validation.v2.extractor.event.EventPathExtractor


class EventValidatorV2(
    schemaRepository: SchemaRepository<String>
) : Validator {

    private val cachedSchemaRepository = CachedSchemaRepository(schemaRepository)

    override fun validate(event: RequestEvent): ValidationResult {
        val schema = cachedSchemaRepository.get(SchemaKey(event.name, event.version))

        val schemaTree = SchemaTreeParser.parse(schema)
        val paths = EventPathExtractor.extract(event)

        val validationErrors = validationErrors(schemaTree, paths)

        val encryptedFields = schemaTree.filter { it.value.encrypted }.map { it.value.path }.toMutableSet()

        return ValidationResult(validationErrors.size != 0, validationErrors, encryptedFields)
    }

    private fun validationErrors(
        schemaTree: SchemaTree,
        paths: Map<String, JsonPath>
    ): MutableSet<Throwable> {
        val validationErrors = mutableSetOf<Throwable>()
        for ((jsonPath, scalarNode) in schemaTree) {
            if (jsonPath !in paths) {
                validationErrors.add(InvalidInputException("Path $jsonPath not found in json"))
                continue
            }
            val jsonValue = paths[jsonPath]!!.value
            try {
                scalarNode.validate(jsonValue)
            } catch (e: Exception) {
                validationErrors.add(e)
            }
        }
        return validationErrors
    }

}
