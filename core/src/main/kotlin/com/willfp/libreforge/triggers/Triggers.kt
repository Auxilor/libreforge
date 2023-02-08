package com.willfp.libreforge.triggers

object Triggers {
    private val registry = mutableMapOf<String, Trigger>()
    private val groupRegistry = mutableMapOf<String, TriggerGroup>()

    /**
     * Get a trigger by [id].
     */
    fun getByID(id: String): Trigger? {
        for ((prefix, group) in groupRegistry) {
            if (id.startsWith("${prefix}_")) {
                return group.create(id.removePrefix("${prefix}_"))
            }
        }

        return registry[id.lowercase()]
    }

    /**
     * Register a new [trigger].
     */
    fun register(trigger: Trigger) {
        registry[trigger.id] = trigger
    }

    /**
     * Register a new [triggerGroup].
     */
    fun register(triggerGroup: TriggerGroup) {
        groupRegistry[triggerGroup.prefix] = triggerGroup
    }

    /**
     * Get a predicate requiring certain trigger parameters.
     */
    fun withParameters(parameters: Set<TriggerParameter>): (Trigger) -> Boolean {
        return {
            it.parameters.flatMap { param -> param.inheritsFrom.toList().plusElement(param) }.containsAll(parameters)
        }
    }

    init {

    }
}
