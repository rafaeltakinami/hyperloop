package br.com.guiabolso.hyperloop.schemas.parser.tree


class SchemaTree(
    private val treeMap: Map<String, ScalarNode>
) : Iterable<Map.Entry<String, ScalarNode>> {

    override fun iterator(): Iterator<Map.Entry<String, ScalarNode>> {
        return treeMap.iterator()
    }

    fun findNode(jsonPath: String): ScalarNode? {
        val cleanJsonPath = jsonPath.replace(Regex("\\[\\d+]"), "")

        return treeMap[cleanJsonPath]
    }
}