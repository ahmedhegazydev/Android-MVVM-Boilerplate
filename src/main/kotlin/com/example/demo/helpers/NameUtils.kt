package com.example.demo.helpers

object NameUtils {

    fun String.toPascalCase(): String {
        if (this.isEmpty()) return this
        // Example: "user details" -> "UserDetails"
        return split(" ", "_", "-")
            .filter { it.isNotBlank() }
            .joinToString("") { it.lowercase().replaceFirstChar(Char::uppercase) }
    }

    fun String.toCamelCase(): String {
        val pascal = toPascalCase()
        return pascal.replaceFirstChar { it.lowercase() }
    }

    fun String.toSnakeCase(): String {
        // "UserDetails" -> "user_details"
        return fold(StringBuilder()) { acc, c ->
            if (c.isUpperCase() && acc.isNotEmpty()) {
                acc.append('_')
            }
            acc.append(c.lowercaseChar())
        }.toString()
    }
}
