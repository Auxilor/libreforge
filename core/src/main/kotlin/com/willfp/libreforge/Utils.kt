package com.willfp.libreforge

inline fun <reified T : Enum<T>> enumValueOfOrNull(name: String): T? {
    return try {
        enumValueOf<T>(name)
    } catch (e: IllegalArgumentException) {
        null
    }
}

val Any.deprecationMessage: String?
    get() {
        val annotation = this::class.java.getAnnotation(Deprecated::class.java)
        return annotation?.message
    }
