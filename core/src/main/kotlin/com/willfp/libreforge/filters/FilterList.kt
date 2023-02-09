package com.willfp.libreforge.filters

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.effects.RunOrder
import com.willfp.libreforge.triggers.TriggerData

/**
 * A list of filters.
 */
class FilterList(
    filters: List<FilterBlock<*, *>>
) : DelegatedList<FilterBlock<*, *>>() {
    init {
        for (order in RunOrder.values()) {
            this.list += filters.filter { it.filter.runOrder == order }
        }
    }

    fun isMet(data: TriggerData) =
        this.all { it.isMet(data) }
}
