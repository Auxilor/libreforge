package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.triggers.TriggerData

abstract class FilterComponent {
    init {
        register()
    }

    private fun register() {
        Filters.addNewFilter(this)
    }

    abstract fun passes(data: TriggerData, config: Config): Boolean

    protected fun <T : Any?> Config.withInverse(
        configName: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean
    ): Boolean {
        val regularPresent = this.has(configName)
        val inversePresent = this.has("!$configName")
        val notInversePresent = this.has("not_$configName")
        if (!regularPresent && !inversePresent && !notInversePresent) {
            return true
        }

        if (inversePresent) {
            return !predicate(this.getter("!$configName"))
        }

        if (notInversePresent) {
            return !predicate(this.getter("not_$configName"))
        }

        return predicate(this.getter(configName))
    }
}
