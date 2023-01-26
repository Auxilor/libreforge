package com.willfp.libreforge.filters

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData

/**
 * A list of filters.
 */
class FilterList(
    mutators: List<FilterBlock<*, *>>
) : DelegatedList<FilterBlock<*, *>>() {
    init {
        for (order in RunOrder.values()) {
            this.list += mutators.filter { it.filter.runOrder == order }
        }
    }

    fun filter(data: TriggerData) =
        this.any { !it.filter(data) }
}
