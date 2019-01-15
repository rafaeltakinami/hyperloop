package br.com.guiabolso.hyperloop

import br.com.guiabolso.hyperloop.schemas.parser.tree.SchemaTreeParser
import br.com.guiabolso.hyperloop.validation.v1.PrimitiveTypes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SchemaTreeParserTest {

    @Test
    fun `test can find scalar node`() {
        val schema = loadSchemaFromFile("/schema_V2_test.yaml")

        val schemaTree = SchemaTreeParser.parse(schema)

        val node = schemaTree.findNode("$.identity.userId")

        assertEquals(PrimitiveTypes.LONG, node!!.type)
        assertEquals("$.identity.userId", node.path)
        assertNotNull(schemaTree)

    }

    @Test
    fun `test can find scalar node of array`() {
        val schema = loadSchemaFromFile("/schema_V2_test.yaml")

        val schemaTree = SchemaTreeParser.parse(schema)

        val node = schemaTree.findNode("$.payload.properties[2].value")

        assertNotNull(schemaTree)
        assertNotNull(node)
        assertEquals(PrimitiveTypes.STRING, node!!.type)
        assertEquals("$.payload.properties.value", node.path)

    }

    @Test
    fun `test cannot find node`() {
        val schema = loadSchemaFromFile("/schema_V2_test.yaml")

        val schemaTree = SchemaTreeParser.parse(schema)

        assertNull(schemaTree.findNode("$.identity.jairaoLindao"))
    }

    private fun loadSchemaFromFile(path: String): String {
        return this::class.java.getResourceAsStream(path).bufferedReader().use { it.readText() }
    }
}