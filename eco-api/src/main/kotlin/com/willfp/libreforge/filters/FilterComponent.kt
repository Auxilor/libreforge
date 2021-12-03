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
}
