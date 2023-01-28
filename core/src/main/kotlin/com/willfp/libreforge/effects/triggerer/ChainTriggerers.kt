package com.willfp.libreforge.effects.triggerer

import com.willfp.libreforge.effects.triggerer.impl.CycleTriggererFactory
import com.willfp.libreforge.effects.triggerer.impl.NormalTriggererFactory
import com.willfp.libreforge.effects.triggerer.impl.RandomTriggererFactory

object ChainTriggerers {
    private val registry = mutableMapOf<String, ChainTriggererFactory>()

    /**
     * Get triggerer by ID, or null if invalid ID is provided.
     *
     * Returns normal if the ID is null.
     */
    fun getByID(id: String?): ChainTriggerer? {
        return if (id != null) registry[id]?.create() else NormalTriggererFactory.create()
    }

    /**
     * Register new factory.
     */
    fun register(factory: ChainTriggererFactory) {
        registry[factory.id] = factory
    }

    init {
        register(NormalTriggererFactory)
        register(CycleTriggererFactory)
        register(RandomTriggererFactory)
    }
}
