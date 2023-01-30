package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ViolationContext

object Filters {
    private val registry = mutableMapOf<String, Filter<*, *>>()

    /**
     * Get a filter by [id].
     */
    fun getByID(id: String): Filter<*, *>? {
        return registry[id]
    }

    /**
     * Register a new [filter].
     */
    fun register(filter: Filter<*, *>) {
        registry[filter.id] = filter
    }

    /**
     * Compile a [config] into a FilterList a given [context].
     */
    fun compile(config: Config, context: ViolationContext): FilterList {
        val blocks = mutableListOf<FilterBlock<*, *>>()

        for (key in config.getKeys(false)) {
            val filter = getByID(key) ?: continue
            blocks += makeBlock(filter, config, context) ?: continue
        }

        return FilterList(blocks)
    }

    private fun <T, C> makeBlock(
        filter: Filter<T, C>,
        config: Config,
        context: ViolationContext
    ): FilterBlock<T, C>? {
        if (!filter.checkConfig(config, context)) {
            return null
        }

        val compileData = filter.makeCompileData(config, context, filter.getValues(config, filter.id))
        return FilterBlock(filter, config, compileData)
    }

    init {

    }
}
