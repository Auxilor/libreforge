package com.willfp.libreforge.filters

import com.willfp.libreforge.DelegatedList
import com.willfp.libreforge.triggers.TriggerData

/**
 * A list of filters.
 */
class FilterList(
    filters: List<FilterBlock<*, *>>
) : DelegatedList<FilterBlock<*, *>>(
    filters.sortedBy {
        it.filter.runOrder.weight
    }
) {
    fun isMet(data: TriggerData) =
        this.all { it.isMet(data) }
}
