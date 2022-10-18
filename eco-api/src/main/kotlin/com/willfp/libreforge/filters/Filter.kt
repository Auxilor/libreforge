package com.willfp.libreforge.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.separatorAmbivalent
import com.willfp.libreforge.triggers.TriggerData

abstract class Filter {
    init {
        register()
    }

    private fun register() {
        Filters.addNewFilter(this)
    }

    abstract fun passes(data: TriggerData, config: Config): Boolean

    protected fun <T : Any> Config.withInverse(
        key: String,
        getter: Config.(String) -> T,
        predicate: (T) -> Boolean
    ): Boolean {
        // Extra ambivalence here just to make sure
        val ambivalent = this.separatorAmbivalent()

        val regularPresent = ambivalent.has(key)
        val inversePresent = ambivalent.has("not_$key")

        if (!regularPresent && !inversePresent) {
            return true
        }

        if (inversePresent) {
            return !predicate(ambivalent.getter("not_$key"))
        }

        return predicate(ambivalent.getter(key))
    }
}
