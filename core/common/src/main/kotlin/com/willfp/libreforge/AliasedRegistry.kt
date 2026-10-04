package com.willfp.libreforge

import com.willfp.eco.core.registry.KRegistrable
import com.willfp.eco.core.registry.Registry

interface Aliased : KRegistrable {
    /**
     * Alternative IDs that resolve to this element, such as IDs it was previously known by.
     */
    val aliases: Set<String>
}

/**
 * A registry that also resolves elements by their [Aliased.aliases].
 *
 * When an ID and an alias clash, the most recently registered element wins.
 */
abstract class AliasedRegistry<T : Aliased> : Registry<T>() {
    private val aliasRegistry = mutableMapOf<String, T>()

    override fun get(id: String): T? {
        return aliasRegistry[id] ?: super.get(id)
    }

    override fun onRegister(element: T) {
        aliasRegistry.remove(element.id)

        for (alias in element.aliases) {
            aliasRegistry[alias] = element
        }
    }

    override fun onRemove(element: T) {
        aliasRegistry.values.removeIf { it == element }
    }
}
