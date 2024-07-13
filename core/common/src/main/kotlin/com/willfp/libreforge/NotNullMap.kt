package com.willfp.libreforge

/**
 * Map that throws NPE if key is not present.
 */
class NotNullMap<K, V>(private val handle: Map<K, V>) : Map<K, V> by handle {
    override fun get(key: K): V {
        return handle[key]!!
    }
}

inline fun <reified K, reified V> Map<K, V>.toNotNullMap(): NotNullMap<K, V> =
    NotNullMap(this)

inline fun <reified K, reified V> notNullMapOf(vararg pairs: Pair<K, V>): NotNullMap<K, V> =
    mapOf(*pairs).toNotNullMap()

/**
 * Mutable version of [NotNullMap].
 */
class NotNullMutableMap<K, V>(private val handle: MutableMap<K, V>) : MutableMap<K, V> by handle {
    override fun get(key: K): V {
        return handle[key]!!
    }
}

inline fun <reified K, reified V> Map<K, V>.toNotNullMutableMap(): NotNullMutableMap<K, V> =
    NotNullMutableMap(this.toMutableMap())

inline fun <reified K, reified V> notNullMutableMapOf(): NotNullMutableMap<K, V> =
    mutableMapOf<K, V>().toNotNullMutableMap()

inline fun <reified K, reified V> notNullMutableMapOf(vararg pairs: Pair<K, V>): NotNullMutableMap<K, V> =
    mutableMapOf(*pairs).toNotNullMutableMap()
