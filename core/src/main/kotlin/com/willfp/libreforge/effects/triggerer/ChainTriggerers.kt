package com.willfp.libreforge.effects.triggerer

object ChainTriggerers {
    private val registry = mutableMapOf<String, ChainTriggererFactory>()

    /**
     * Get triggerer by ID, or null if invalid ID is provided.
     */
    fun getByID(id: String): ChainTriggerer? {
        return registry[id]?.create()
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
