package br.com.guiabolso.schemas

object Regex {

    val PARSE_WORKFLOW_KEY = """^([a-zA-Z0-9:_.-]+/)+([a-zA-Z0-9:_.-]+)_V(\d+).(yaml)$""".toRegex()
}