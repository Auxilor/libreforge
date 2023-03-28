package com.willfp.libreforge

/**
 * Map that throws NPE if key is not present.
 */
class NotNullMap<K, V>(private val handle: MutableMap<K, V>) : MutableMap<K, V> by handle {
    override fun get(key: K): V {
        return handle[key]!!
    }
}

inline fun <reified K, reified V> Map<K, V>.toNotNullMap(): NotNullMap<K, V> =
    NotNullMap(this.toMutableMap())

inline fun <reified K, reified V> notNullMapOf(): NotNullMap<K, V> =
    mutableMapOf<K, V>().toNotNullMap()

inline fun <reified K, reified V> notNullMapOf(vararg pairs: Pair<K, V>): NotNullMap<K, V> =
    mutableMapOf(*pairs).toNotNullMap()
