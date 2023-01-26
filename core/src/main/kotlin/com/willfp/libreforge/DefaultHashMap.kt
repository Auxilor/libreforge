package com.willfp.libreforge

/**
 * HashMap with a default value.
 */
class DefaultHashMap<K, V>(
    private val default: V
) : MutableMap<K, V> by HashMap() {
    override fun get(key: K): V =
        this.getOrDefault(key, default)
}
