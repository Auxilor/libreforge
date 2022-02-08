package com.willfp.libreforge.triggers

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty

abstract class DataMutator(
    id: String,
    val order: MutationOrder = MutationOrder.NORMAL
) : ConfigurableProperty(id) {
    init {
        register()
    }

    private fun register() {
        DataMutators.addNewMutator(this)
    }

    abstract fun mutate(data: TriggerData, config: Config): TriggerData
}

data class ConfiguredDataMutator(
    val mutator: DataMutator,
    val config: Config
) {
    operator fun invoke(data: TriggerData): TriggerData =
        mutator.mutate(data, config)
}

enum class MutationOrder {
    EARLY,
    NORMAL,
    LATE
}

fun Collection<ConfiguredDataMutator>.mutateInOrder(data: TriggerData): TriggerData {
    var currentData = data

    for (order in MutationOrder.values()) {
        for (mutator in this.filter { it.mutator.order == order }) {
            currentData = mutator(currentData)
        }
    }

    return currentData
}
