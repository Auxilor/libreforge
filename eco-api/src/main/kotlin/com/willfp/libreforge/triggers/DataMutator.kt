package com.willfp.libreforge.triggers

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigurableProperty

abstract class DataMutator(
    id: String
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

fun Collection<ConfiguredDataMutator>.mutate(data: TriggerData): TriggerData {
    var currentData = data
    for (mutator in this) {
        currentData = mutator(currentData)
    }

    return currentData
}
