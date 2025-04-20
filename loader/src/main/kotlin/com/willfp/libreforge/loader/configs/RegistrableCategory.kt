package com.willfp.libreforge.loader.configs

import com.willfp.eco.core.registry.Registrable
import com.willfp.eco.core.registry.Registry

/**
 * A shorthand way of creating config categories that have their own registries.
 */
abstract class RegistrableCategory<T : Registrable>(
    id: String,
    directory: String
) : ConfigCategory(id, directory) {
    /**
     * The registry.
     */
    protected val registry = Registry<T>()

    /**
     * Get an element by [id].
     */
    operator fun get(id: String?): T? = getByID(id)

    /**
     * Get an element by [id].
     */
    fun getByID(id: String?): T? = id?.let { registry[id] }

    /**
     * Get all elements.
     */
    fun values(): Set<T> = registry.values()
}
