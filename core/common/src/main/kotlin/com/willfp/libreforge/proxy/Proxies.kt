package com.willfp.libreforge.proxy

import com.willfp.eco.core.Prerequisite

private const val BASE_PACKAGE = "com.willfp.libreforge.proxy"

private val VERSION_PACKAGE = if (Prerequisite.HAS_1_21.isMet) {
    "modern"
} else "legacy"

internal annotation class Proxy(
    val location: String
)

private class InvalidProxyException(message: String) : Exception(message)

private val cache = mutableMapOf<Class<*>, Any>()

internal fun <T> loadProxy(clazz: Class<T>): T {
    @Suppress("UNCHECKED_CAST")
    return cache.getOrPut(clazz) {
        loadProxyUncached(clazz)
    } as T
}

private fun loadProxyUncached(clazz: Class<*>): Any {
    val proxy = clazz.getAnnotation(Proxy::class.java)
    val location = proxy?.location ?: throw IllegalArgumentException("Class ${clazz.name} is not a proxy")
    val className = "$BASE_PACKAGE.$VERSION_PACKAGE.$location"

    try {
        val found = Class.forName(className)
        val constructor = found.getConstructor()
        val instance = constructor.newInstance()

        return instance
    } catch (e: ClassNotFoundException) {
        throw InvalidProxyException("Could not find proxy class $className")
    } catch (e: NoSuchMethodException) {
        throw InvalidProxyException("Could not find no-args constructor for proxy class $className")
    }
}
