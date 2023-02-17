package com.willfp.libreforge

/**
 * HashMap with a default value.
 */
open class DefaultHashMap<K, V>(
    private val default: V,
    private val map: MutableMap<K, V> = HashMap()
) : MutableMap<K, V> by map {
    override fun get(key: K): V =
        map.getOrPut(key) { default }

    override fun toString() = map.toString()
}

/**
 * Key to MutableList HashMap.
 */
class KeyToMutableListMap<K, V> : DefaultHashMap<K, MutableList<V>>(mutableListOf())

/**
 * Key to MutableMap HashMap.
 */
class KeyToMutableMapMap<K, K1, V> : DefaultHashMap<K, MutableMap<K1, V>>(mutableMapOf())

/**
 * Key to KeyToMutableListMap HashMap.
 */
class KeyToMutableListMapMap<K, K1, V> : DefaultHashMap<K, KeyToMutableListMap<K1, V>>(KeyToMutableListMap())
