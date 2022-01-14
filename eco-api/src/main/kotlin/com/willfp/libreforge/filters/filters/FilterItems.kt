package com.willfp.libreforge.filters.filters

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.items.Items
import com.willfp.libreforge.filters.FilterComponent
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.wrappers.WrappedDropEvent

class FilterItems : FilterComponent() {
    override fun passes(data: TriggerData, config: Config): Boolean {
        val event = data.event as? WrappedDropEvent<*> ?: return true
        val items = event.items
        val allowed = config.getStringsOrNull("items")?.map { Items.lookup(it) }
            ?: return true
        for (testableItem in allowed) {
            for (item in items) {
                if (testableItem.matches(item)) {
                    return true
                }
            }
        }

        return false
    }
}
