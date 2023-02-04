package com.willfp.libreforge.triggers

object Triggers {
    private val registry = mutableMapOf<String, Trigger>()

    /**
     * Get a trigger by [id].
     */
    fun getByID(id: String): Trigger? {
        return registry[id]
    }

    /**
     * Register a new [trigger].
     */
    fun register(trigger: Trigger) {
        registry[trigger.id] = trigger
    }

    /**
     * Get a predicate requiring certain trigger parameters.
     */
    fun withParameters(parameters: Set<TriggerParameter>): (Trigger) -> Boolean {
        return {
            it.parameters.containsAll(parameters)
        }
    }

    init {

    }
}
