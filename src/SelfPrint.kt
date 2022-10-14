fun main() {
	fun quote(s: String?) = if (s == null) "null" else '"' + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("$", "\\$") + '"'
	val commands = listOf("fun main() {", "	fun quote(s: String?) = if (s == null) \"null\" else '\"' + s.replace(\"\\\\\", \"\\\\\\\\\").replace(\"\\\"\", \"\\\\\\\"\").replace(\"\$\", \"\\\\\$\") + '\"'", null, "	for (command in commands) println(command ?: \"	val commands = listOf(\${commands.joinToString { quote(it) }})\")", "}")
	for (command in commands) println(command ?: "	val commands = listOf(${commands.joinToString { quote(it) }})")
}
