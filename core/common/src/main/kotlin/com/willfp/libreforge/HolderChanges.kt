package com.willfp.libreforge

/**
 * The keys of one or more providers' answers, classified against what was applied.
 */
internal open class KeyChanges<K, V> {
    val added = ArrayList<Pair<K, V>>()
    val removed = ArrayList<Pair<K, V>>()
    val moved = HashSet<K>()

    /**
     * Classify the keys of a provider's [new] answer against its [old] one. A key whose holder
     * changed is removed and added; a key whose slot changed is moved.
     */
    fun classify(old: Map<K, V>, new: Map<K, V>, holderOf: (V) -> Any?, slotOf: (V) -> Any?) {
        for ((key, value) in new) {
            val previous = old[key]
            when {
                previous == null -> added += key to value
                holderOf(previous) != holderOf(value) -> {
                    removed += key to previous
                    added += key to value
                }

                slotOf(previous) != slotOf(value) -> moved += key
            }
        }

        for ((key, previous) in old) {
            if (!new.containsKey(key)) {
                removed += key to previous
            }
        }
    }
}

/**
 * If a provider's [new] answer is the same as its [old] one: the same keys in the same order, with
 * equal values in the same slots.
 */
internal fun <K, V> isSameAnswer(old: Map<K, V>, new: Map<K, V>, slotOf: (V) -> Any?): Boolean {
    if (old.size != new.size) {
        return false
    }

    val oldEntries = old.entries.iterator()
    for ((key, value) in new) {
        val (oldKey, oldValue) = oldEntries.next()
        if (oldKey != key || oldValue != value || slotOf(oldValue) != slotOf(value)) {
            return false
        }
    }

    return true
}
