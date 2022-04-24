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

        return config.withInverse("items", Config::getStringsOrNull) {
            it?.map { lookup -> Items.lookup(lookup) }?.any { testableItem ->
                items.any { item -> testableItem.matches(item) }
            } == true
        }
    }
}
