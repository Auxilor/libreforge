package com.willfp.libreforge

/**
 * HashMap with a default value.
 */
open class DefaultHashMap<K, V>(
    private val default: V
) : MutableMap<K, V> by HashMap() {
    override fun get(key: K): V =
        this.getOrDefault(key, default)
}

/**
 * Key to MutableList HashMap.
 */
class ListedHashMap<K, V> : DefaultHashMap<K, MutableList<V>>(mutableListOf())

/**
 * Key to MutableMap HashMap.
 */
class MappedHashMap<K, K1, V> : DefaultHashMap<K, MutableMap<K1, V>>(mutableMapOf())

/**
 * Key to ListedHashMap HashMap.
 */
class MappedListedHashMap<K, K1, V> : DefaultHashMap<K, ListedHashMap<K1, V>>(ListedHashMap())
