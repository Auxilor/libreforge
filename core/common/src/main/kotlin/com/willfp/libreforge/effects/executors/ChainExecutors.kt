package com.willfp.libreforge.effects.executors

import com.willfp.libreforge.effects.executors.impl.CycleExecutorFactory
import com.willfp.libreforge.effects.executors.impl.NormalExecutorFactory
import com.willfp.libreforge.effects.executors.impl.RandomExecutorFactory

object ChainExecutors {
    private val registry = mutableMapOf<String, ChainExecutorFactory>()

    /**
     * Get executor by ID, or null if invalid ID is provided.
     *
     * Returns normal if the ID is null.
     */
    fun getByID(id: String?): ChainExecutor {
        return if (id != null) registry[id]?.create() ?: NormalExecutorFactory.create()
        else NormalExecutorFactory.create()
    }

    /**
     * Register new factory.
     */
    fun register(factory: ChainExecutorFactory) {
        registry[factory.id] = factory
    }

    init {
        register(NormalExecutorFactory)
        register(CycleExecutorFactory)
        register(RandomExecutorFactory)
    }
}
